package org.netmelody.cieye.core.domain;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.unmodifiableIterable;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.base.Function;

public final class TargetGroup implements Iterable<Target>{

    private final Iterable<Target> targets;
    
    public TargetGroup() {
        this(new ArrayList<Target>());
    }
    
    public TargetGroup(TargetDigestGroup targetsConstituting) {
        this(transform(targetsConstituting.targets(), toTargets()));
    }

    public TargetGroup(Iterable<Target> targets) {
        this.targets = (null == targets) ? new ArrayList<Target>() : targets;
    }

    public TargetGroup add(TargetGroup group) {
        if (null == group) {
            return this;
        }
        return new TargetGroup(concat(targets, group.targets));
    }

    public Iterable<Target> targets() {
        return unmodifiableIterable(targets);
    }
    
    private static Function<TargetDigest, Target> toTargets() {
        return new Function<TargetDigest, Target>() {
            @Override public Target apply(TargetDigest digest) {
                return new Target(digest.id(), digest.webUrl(), digest.name(), digest.status());
            }
        };
    }

    @Override
    public Iterator<Target> iterator() {
        return targets().iterator();
    }
}
