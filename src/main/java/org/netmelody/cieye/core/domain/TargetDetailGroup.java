package org.netmelody.cieye.core.domain;

import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.concat;

public final class TargetDetailGroup implements Iterable<TargetDetail>{

    private final Iterable<TargetDetail> targets;

    public TargetDetailGroup() {
        this(ImmutableList.<TargetDetail>of());
    }

    public TargetDetailGroup(TargetDigestGroup targetsConstituting) {
        this(copyOf(transform(targetsConstituting.targets(), toTargets())));
    }

    public static TargetDetailGroup of(Iterable<TargetDetail> targets) {
        return (null == targets) ? new TargetDetailGroup() : new TargetDetailGroup(copyOf(targets));
    }

    private TargetDetailGroup(Iterable<TargetDetail> targets) {
        this.targets = targets;
    }

    public TargetDetailGroup add(TargetDetailGroup group) {
        if (null == group) {
            return this;
        }
        return new TargetDetailGroup(concat(targets, group.targets));
    }

    public Iterable<TargetDetail> targets() {
        return targets;
    }

    private static Function<TargetDigest, TargetDetail> toTargets() {
        return new Function<TargetDigest, TargetDetail>() {
            @Override public TargetDetail apply(TargetDigest digest) {
                return new TargetDetail(digest.id().id(), digest.webUrl(), digest.featureName(), digest.name(), digest.status(), 0L);
            }
        };
    }

    @Override
    public Iterator<TargetDetail> iterator() {
        return targets().iterator();
    }
}
