package org.netmelody.cii.witness.jenkins.jsondomain;

import java.util.List;

import org.netmelody.cii.domain.Status;

public final class Job {
    public String name;
    public String url;
    public String displayName;
    public String description;
    public String color;
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
    
    public Status status() {
        if (null == color || color.startsWith("blue")) {
            return Status.GREEN;
        }
        if ("disabled".equals(color)) {
            return Status.DISABLED;
        }
        return Status.BROKEN;
    }
    
    public boolean building() {
        return (null != color && color.endsWith("_anime"));
    }
}