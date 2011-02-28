package org.netmelody.cii.witness.jenkins;

import static java.util.Collections.unmodifiableList;
import static org.netmelody.cii.domain.Build.buildAt;
import static org.netmelody.cii.domain.Percentage.percentageOf;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.netmelody.cii.domain.Percentage;
import org.netmelody.cii.domain.Sponsor;
import org.netmelody.cii.domain.Status;
import org.netmelody.cii.domain.Target;
import org.netmelody.cii.persistence.Detective;
import org.netmelody.cii.witness.jenkins.jsondomain.Build;
import org.netmelody.cii.witness.jenkins.jsondomain.ChangeSetItem;
import org.netmelody.cii.witness.jenkins.jsondomain.Job;
import org.netmelody.cii.witness.jenkins.jsondomain.User;

public class JobAnalyser {
    
    private final JenkinsCommunicator communicator;
    private final String jobEndpoint;
    private final Map<String, List<Sponsor>> sponsorCache = new HashMap<String, List<Sponsor>>();
    private final Detective detective;
    private final BuildDurationFetcher durationFetcher;

    public JobAnalyser(JenkinsCommunicator communicator, String jobEndpoint, Detective detective) {
        this.communicator = communicator;
        this.jobEndpoint = jobEndpoint;
        this.detective = detective;
        this.durationFetcher = new BuildDurationFetcher(this.communicator);
    }
    
    public Target analyse() {
        final Job job = communicator.makeJenkinsRestCall(jobEndpoint, Job.class);
        if (job.lastBuild == null) {
            return new Target(job.url, job.name, job.status(), buildAt(percentageOf(0)));
        }
        return new Target(job.url, job.name, statusOf(job), sponsorsOf(job), buildsFor(job));
    }

    private Status statusOf(Job job) {
//        if (!Status.BROKEN.equals(job.status())) {
            return job.status();
//        }
    }
    
    private List<Sponsor> sponsorsOf(Job job) {
        final List<Sponsor> result = new ArrayList<Sponsor>();
        
        long lastSuccessNumber = (job.lastStableBuild == null) ? -1 : job.lastStableBuild.number;
        for (Build build : job.builds) {
            if (build.number > lastSuccessNumber) {
                result.addAll(sponsorsOf(build.url));
            }
        }
        
        return result;
    }
    
    private List<Sponsor> sponsorsOf(String buildUrl) {
        if (null == buildUrl || buildUrl.length() == 0) {
            return new ArrayList<Sponsor>();
        }
        
        if (sponsorCache.containsKey(buildUrl)) {
            return sponsorCache.get(buildUrl);
        }
        
        final Build buildData = fetchBuildData(buildUrl);
        if (null == buildData) {
            return new ArrayList<Sponsor>();
        }
        
        final List<Sponsor> sponsors = detective.sponsorsOf(commitMessagesOf(buildData));
        
        if (sponsors.isEmpty()) {
            for (String upstreamBuildUrl : buildData.upstreamBuildUrls()) {
                sponsors.addAll(sponsorsOf(communicator.endpoint() + "/" + upstreamBuildUrl));
            }
        }
        
        final List<Sponsor> result = unmodifiableList(sponsors);
        if (!buildData.building) {
            sponsorCache.put(buildUrl, result);
        }
        
        return result;
    }
    
    private String commitMessagesOf(Build build) {
        if (null == build.changeSet || null == build.changeSet.items) {
            return "";
        }
        
        final StringBuilder result = new StringBuilder();
        for (ChangeSetItem changeSetItem : build.changeSet.items) {
            result.append(changeSetItem.user);
            result.append(changeSetItem.msg);
        }
        
        for (User user : build.culprits) {
            result.append(user.fullName);
        }
        
        return result.toString();
    }
    
    private List<org.netmelody.cii.domain.Build> buildsFor(final Job job) {
        final List<org.netmelody.cii.domain.Build> result = new ArrayList<org.netmelody.cii.domain.Build>();
        if (!job.building()) {
            return result;
        }
        
        final Build currentBuild = fetchBuildData(job.lastBuild.url);
        final Percentage progress = percentageOf(new Date().getTime() - currentBuild.timestamp,
                                                 durationFetcher.lastGoodDurationOf(job));
        result.add(buildAt(progress));
        
        return result;
    }

    private Build fetchBuildData(String buildUrl) {
        return communicator.makeJenkinsRestCall(buildUrl, Build.class);
    }
}