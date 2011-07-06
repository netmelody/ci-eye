package org.netmelody.cieye.spies.jenkins;

import static java.util.Collections.unmodifiableSet;
import static org.netmelody.cieye.core.domain.Percentage.percentageOf;
import static org.netmelody.cieye.core.domain.RunningBuild.buildAt;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.netmelody.cieye.core.domain.Percentage;
import org.netmelody.cieye.core.domain.Sponsor;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.Target;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.jenkins.jsondomain.Build;
import org.netmelody.cieye.spies.jenkins.jsondomain.BuildDetail;
import org.netmelody.cieye.spies.jenkins.jsondomain.ChangeSetItem;
import org.netmelody.cieye.spies.jenkins.jsondomain.Job;
import org.netmelody.cieye.spies.jenkins.jsondomain.JobDetail;
import org.netmelody.cieye.spies.jenkins.jsondomain.User;

public final class JobAnalyser {
    
    private final JenkinsCommunicator communicator;
    private final String jobEndpoint;
    private final Map<String, Set<Sponsor>> sponsorCache = new HashMap<String, Set<Sponsor>>();
    private final KnownOffendersDirectory detective;
    private final BuildDetailFetcher buildDetailFetcher;
    private final BuildDurationFetcher buildDurationFetcher;
    private final BuildStartTimeFetcher buildStartTimeFetcher;

    public JobAnalyser(JenkinsCommunicator communicator, String jobEndpoint, KnownOffendersDirectory detective) {
        this.communicator = communicator;
        this.jobEndpoint = jobEndpoint;
        this.detective = detective;
        this.buildDetailFetcher = new BuildDetailFetcher(this.communicator);
        this.buildDurationFetcher = new BuildDurationFetcher(this.buildDetailFetcher);
        this.buildStartTimeFetcher = new BuildStartTimeFetcher(this.buildDetailFetcher);
    }
    
    public Target analyse(Job jobDigest) {
        if (!jobDigest.url.equals(jobEndpoint)) {
            throw new IllegalArgumentException("Incorrect job digest");
        }
        if (!jobDigest.building() && Status.BROKEN != jobDigest.status()) {
            sponsorCache.clear();
            return new Target(jobDigest.url, jobDigest.url, jobDigest.name, jobDigest.status());
        }
        return analyse();
    }

    public Target analyse() {
        final JobDetail job = communicator.makeJenkinsRestCall(jobEndpoint, JobDetail.class);
        return new Target(job.url, job.url, job.name, statusOf(job), startTimeOf(job), buildsFor(job), sponsorsOf(job));
    }

    public String lastBadBuildUrl() {
        return communicator.makeJenkinsRestCall(jobEndpoint, JobDetail.class).lastBadBuildUrl();
    }
    
    private long startTimeOf(JobDetail job) {
        return buildStartTimeFetcher.lastStartTimeOf(job);
    }

    private Status statusOf(JobDetail job) {
        if (job.lastBuild == null) {
            return job.status();
        }
        
        if (!Status.BROKEN.equals(job.status())) {
            return job.status();
        }
        
        final String lastBadBuildDesc = this.buildDetailFetcher.detailsOf(job.lastBadBuildUrl()).description;
        if (null == lastBadBuildDesc || lastBadBuildDesc.length() == 0) {
            return job.status();
        }
        return Status.UNDER_INVESTIGATION;
    }
    
    private Set<Sponsor> sponsorsOf(JobDetail job) {
        final Set<Sponsor> result = new HashSet<Sponsor>();
        
        if (job.lastBuild == null) {
            return result;
        }
        
        long lastSuccessNumber = (job.lastStableBuild == null) ? -1 : job.lastStableBuild.number;
        for (Build build : job.builds()) {
            if (build.number > lastSuccessNumber) {
                result.addAll(sponsorsOf(build.url));
            }
        }
        
        return result;
    }
    
    private Set<Sponsor> sponsorsOf(String buildUrl) {
        if (null == buildUrl || buildUrl.length() == 0) {
            return new HashSet<Sponsor>();
        }
        
        if (sponsorCache.containsKey(buildUrl)) {
            return sponsorCache.get(buildUrl);
        }
        
        final BuildDetail buildData = this.buildDetailFetcher.detailsOf(buildUrl);
        if (null == buildData) {
            return new HashSet<Sponsor>();
        }
        
        final Set<Sponsor> sponsors = new HashSet<Sponsor>(detective.search(commitMessagesOf(buildData)));
        
        if (sponsors.isEmpty()) {
            for (String upstreamBuildUrl : buildData.upstreamBuildUrls()) {
                sponsors.addAll(sponsorsOf(communicator.endpoint() + "/" + upstreamBuildUrl));
            }
        }
        
        final Set<Sponsor> result = unmodifiableSet(sponsors);
        if (!buildData.building) {
            sponsorCache.put(buildUrl, result);
        }
        
        return result;
    }
    
    private String commitMessagesOf(BuildDetail build) {
        if (null == build.changeSet || null == build.changeSet.items) {
            return "";
        }
        
        final StringBuilder result = new StringBuilder();
        for (ChangeSetItem changeSetItem : build.changeSet.items) {
            result.append(changeSetItem.user);
            result.append(changeSetItem.msg);
        }
        
        for (User user : build.culprits()) {
            result.append(user.fullName);
        }
        
        return result.toString();
    }
    
    private List<org.netmelody.cieye.core.domain.RunningBuild> buildsFor(final JobDetail job) {
        final List<org.netmelody.cieye.core.domain.RunningBuild> result = new ArrayList<org.netmelody.cieye.core.domain.RunningBuild>();
        
        if (!job.building() || job.lastBuild == null) {
            return result;
        }
        
        final BuildDetail currentBuild = this.buildDetailFetcher.detailsOf(job.lastBuild.url);
        final Percentage progress = percentageOf(new Date().getTime() - currentBuild.timestamp,
                                                 this.buildDurationFetcher.lastGoodDurationOf(job));
        result.add(buildAt(progress));
        
        return result;
    }
}