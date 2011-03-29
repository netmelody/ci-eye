package org.netmelody.cieye.witness.jenkins.jsondomain;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Ordering;

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
        final List<Build> lastBadBuilds = newArrayList(lastUnstableBuild, lastUnsuccessfulBuild, lastFailedBuild);
        final Collection<String> candidates = filter(filter(transform(filter(lastBadBuilds, notNull()), toUrl()), notNull()), notEmpty());
        
        if (candidates.isEmpty()) {
            return url;
        }
        
        return lexographicallyLastFrom(candidates);
    }
    
    private String lexographicallyLastFrom(final Collection<String> candidates) {
        return new Ordering<String>() {
            @Override public int compare(String left, String right) {
                return left.compareTo(right);
            }
        }.max(candidates);
    }
    
    private Predicate<String> notEmpty() {
        return new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return input.length() > 0;
            }
        };
    }
    
    private Function<Build, String> toUrl() {
        return new Function<Build, String>() {
            @Override
            public String apply(Build input) {
                return input.url;
            }
        };
    }
}
