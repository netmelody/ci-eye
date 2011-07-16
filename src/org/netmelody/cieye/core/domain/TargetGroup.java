package org.netmelody.cieye.core.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public final class TargetGroup {

    private final List<Target> targets = new ArrayList<Target>();
    
    public TargetGroup() {
        this(new ArrayList<Target>());
    }
    
    public TargetGroup(TargetDigestGroup targetsConstituting) {
        this(Collections2.transform(targetsConstituting.targets(), toTargets()));
    }

    public TargetGroup(Collection<Target> targets) {
        this.targets.addAll(targets);
    }

    public TargetGroup add(TargetGroup group) {
        if (null == group) {
            return this;
        }
        final TargetGroup result = new TargetGroup(targets);
        result.targets.addAll(group.targets);
        return result;
    }

    public List<Target> targets() {
        return new ArrayList<Target>(targets);
    }
    
    private static Function<TargetDigest, Target> toTargets() {
        return new Function<TargetDigest, Target>() {
            @Override public Target apply(TargetDigest digest) {
                return new Target(digest.id(), digest.webUrl(), digest.name(), digest.status());
            }
        };
    }
}
