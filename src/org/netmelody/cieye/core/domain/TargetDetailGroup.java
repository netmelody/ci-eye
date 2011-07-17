package org.netmelody.cieye.core.domain;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.unmodifiableIterable;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.base.Function;

public final class TargetDetailGroup implements Iterable<TargetDetail>{

    private final Iterable<TargetDetail> targets;
    
    public TargetDetailGroup() {
        this(new ArrayList<TargetDetail>());
    }
    
    public TargetDetailGroup(TargetDigestGroup targetsConstituting) {
        this(transform(targetsConstituting.targets(), toTargets()));
    }

    public TargetDetailGroup(Iterable<TargetDetail> targets) {
        this.targets = (null == targets) ? new ArrayList<TargetDetail>() : targets;
    }

    public TargetDetailGroup add(TargetDetailGroup group) {
        if (null == group) {
            return this;
        }
        return new TargetDetailGroup(concat(targets, group.targets));
    }

    public Iterable<TargetDetail> targets() {
        return unmodifiableIterable(targets);
    }
    
    private static Function<TargetDigest, TargetDetail> toTargets() {
        return new Function<TargetDigest, TargetDetail>() {
            @Override public TargetDetail apply(TargetDigest digest) {
                return new TargetDetail(digest.id().id(), digest.webUrl(), digest.name(), digest.status());
            }
        };
    }

    @Override
    public Iterator<TargetDetail> iterator() {
        return targets().iterator();
    }
}
