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

    public JobAnalyser(JenkinsCommunicator communicator, String jobEndpoint) {
        this.communicator = communicator;
        this.jobEndpoint = jobEndpoint;
    }
    
    public Percentage progressOf(Job job) {
        if (!job.building()) {
            return percentageOf(100);
        }
        
        final Build lastBuild = fetchBuildData(job.lastBuild.url);
        final Build lastSuccessfulBuild = fetchBuildData(job.lastSuccessfulBuild.url);
        
        return percentageOf(new Date().getTime() - lastBuild.timestamp,
                            lastSuccessfulBuild.duration);
    }
    
    public List<Sponsor> sponsorsOf(Job job) {
        return sponsorsOf(job.lastBuild.url);
    }
    
    private List<Sponsor> sponsorsOf(String buildUrl) {
        if (null == buildUrl || buildUrl.isEmpty()) {
            return new ArrayList<Sponsor>();
        }
        
        if (sponsorCache.containsKey(buildUrl)) {
            return sponsorCache.get(buildUrl);
        }
        
        final Build buildData = fetchBuildData(buildUrl);
        if (null == buildData) {
            return new ArrayList<Sponsor>();
        }
        
        final Detective detective = new Detective();
        final List<Sponsor> sponsors = detective.sponsorsOf(analyseChanges(buildData));
        
        if (!sponsors.isEmpty()) {
            return sponsors;
        }
        
        for (String upstreamBuildUrl :  buildData.upstreamBuildUrls()) {
            sponsors.addAll(sponsorsOf(communicator.endpoint() + "/" + upstreamBuildUrl));
        }
        
        final List<Sponsor> result = unmodifiableList(sponsors);
        if (!buildData.building) {
            sponsorCache.put(buildUrl, result);
        }
        
        return result;
    }
    
    private String analyseChanges(Build build) {
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
    
    private Build fetchBuildData(String buildUrl) {
        return communicator.makeJenkinsRestCall(buildUrl, Build.class);
    }
    
    public Target analyse() {
        final Job job = communicator.makeJenkinsRestCall(jobEndpoint, Job.class);
        if (job.lastBuild == null || job.lastSuccessfulBuild == null) {
            return new Target(job.name, job.status(), buildAt(percentageOf(0)));
        }

        org.netmelody.cii.domain.Build builds[] = new org.netmelody.cii.domain.Build[0];
        if (job.building()) {
            builds = new org.netmelody.cii.domain.Build[] { buildAt(progressOf(job)) };
        }
        
        return new Target(job.name, job.name, job.status(), sponsorsOf(job), builds);
    }
}