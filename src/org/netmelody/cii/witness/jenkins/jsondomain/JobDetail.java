package org.netmelody.cii.witness.jenkins.jsondomain;

import java.util.List;

public final class JobDetail extends Job {
    public String displayName;
    public String description;
    public boolean buildable;
    public boolean inQueue;
    public boolean keepDependencies;
    public boolean concurrentBuild;
    public List<Build> builds;
    public List<Action> actions;
    public Build firstBuild;
    public Build lastBuild;
    public Build lastFailedBuild;
    public Build lastStableBuild;
    public Build lastSuccessfulBuild;
    public Build lastUnstableBuild;
    public Build lastUnsuccessfulBuild;
    public int nextBuildNumber;
    public List<Project> downstreamProjects;
    public List<Project> upstreamProjects;
    public List<HealthReport> healthReport;
    //scm
    //property
    //queueItem
    
    public String lastBadBuildUrl() {
        if (null == lastUnstableBuild) {
            if (null == lastFailedBuild) {
                return lastFailedBuild.url;
            }
            return url;
        }
        
        if (null == lastFailedBuild) {
            return lastUnstableBuild.url;
        }
        
        return (lastUnstableBuild.number >= lastFailedBuild.number) ? lastUnstableBuild.url : lastFailedBuild.url;
    }
}
