package org.netmelody.cieye.spies.jenkins.jsondomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;

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
        final Collection<Build> candidates = filter(newArrayList(lastUnstableBuild, lastUnsuccessfulBuild, lastFailedBuild), valid());
        
        if (candidates.isEmpty()) {
            return "";
        }
        
        return lastFrom(candidates).url;
    }
    
    private Predicate<Build> valid() {
        return new Predicate<Build>() {
            @Override public boolean apply(Build build) {
                return (build != null) && (!Strings.isNullOrEmpty(build.url)) && build.number >= 0; 
            }
        };
    }

    public List<Build> builds() {
        return (builds == null) ? new ArrayList<Build>() : builds;
    }
    
    private Build lastFrom(final Collection<Build> candidates) {
        return new Ordering<Build>() {
            @Override public int compare(Build left, Build right) {
                return Longs.compare(left.number, right.number);
            }
        }.max(candidates);
    }
}
