package org.netmelody.cieye.core.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;

public final class TargetDetail extends Target {

    private final long lastStartTime;
    private final Set<Sponsor> sponsors = new HashSet<Sponsor>();
    private final List<RunningBuild> builds = new ArrayList<RunningBuild>();
    
    public TargetDetail(String id, String webUrl, String name, Status status, long lastStartTime) {
        this(id, webUrl, name, status, lastStartTime, new ArrayList<RunningBuild>(), new HashSet<Sponsor>());
    }
    
    public TargetDetail(String id, String webUrl, String name, Status status, long lastStartTime, Collection<RunningBuild> builds, Set<Sponsor> sponsors) {
        super(id, webUrl, name, statusFrom(builds, status));
        this.lastStartTime = lastStartTime;
        this.sponsors.addAll(sponsors);
        this.builds.addAll(builds);
    }

    private static Status statusFrom(Collection<RunningBuild> builds, Status parentStatus) {
        final Status result = Status.RANK.max(concat(transform(builds, toStatus()), newArrayList(parentStatus)));
        return Status.UNKNOWN.equals(result) ? parentStatus : result;
    }

    private static Function<RunningBuild, Status> toStatus() {
        return new Function<RunningBuild, Status>() {
            @Override
            public Status apply(RunningBuild build) {
                return build.status();
            }
        };
    }

    public long lastStartTime() {
        return lastStartTime;
    }
    
    public Set<Sponsor> sponsors() {
        return new HashSet<Sponsor>(sponsors);
    }

    public List<RunningBuild> builds() {
        return new ArrayList<RunningBuild>(builds);
    }

    public TargetDetail withBuilds(List<RunningBuild> builds) {
        return new TargetDetail(id().id(), webUrl(), name(), status(), lastStartTime, builds, sponsors);
    }

    public TargetDetail withStatus(Status newStatus) {
        return new TargetDetail(id().id(), webUrl(), name(), newStatus, lastStartTime, builds, sponsors);
    }
}