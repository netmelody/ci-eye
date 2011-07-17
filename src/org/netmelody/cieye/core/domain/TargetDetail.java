package org.netmelody.cieye.core.domain;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Iterables.find;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public final class TargetDetail {

    private final String id;
    private final String webUrl;
    private final String name;
    private final Status status;
    private final long lastStartTime;
    private final Set<Sponsor> sponsors = new HashSet<Sponsor>();
    private final List<RunningBuild> builds = new ArrayList<RunningBuild>();
    
    public TargetDetail(String id, String webUrl, String name, Status status) {
        this(id, webUrl, name, status, 0L, new ArrayList<RunningBuild>(), new HashSet<Sponsor>());
    }
    
    public TargetDetail(String id, String webUrl, String name, Status status, long lastStartTime, Collection<RunningBuild> builds, Set<Sponsor> sponsors) {
        this.id = id;
        this.webUrl = webUrl;
        this.name = name;
        this.lastStartTime = lastStartTime;
        this.sponsors.addAll(sponsors);
        this.builds.addAll(builds);
        this.status = find(transform(builds, toStatus()), isBroken(), status);
    }

    private Predicate<Status> isBroken() {
        return new Predicate<Status>() {
            @Override public boolean apply(Status status) {
                return Status.BROKEN.equals(status);
            }
        };
    }

    private Function<RunningBuild, Status> toStatus() {
        return new Function<RunningBuild, Status>() {
            @Override
            public Status apply(RunningBuild build) {
                return build.status();
            }
        };
    }

    public String id() {
        return id;
    }
    
    public String name() {
        return name;
    }
    
    public Status status() {
        return status;
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
        return new TargetDetail(id, webUrl, name, status, lastStartTime, builds, sponsors);
    }

    public TargetDetail withStatus(Status newStatus) {
        return new TargetDetail(id, webUrl, name, newStatus, lastStartTime, builds, sponsors);
    }
}
