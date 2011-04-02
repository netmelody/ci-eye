package org.netmelody.cieye.core.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class TargetGroup {

    private final List<Target> targets = new ArrayList<Target>();
    
    public TargetGroup() {
        this(new ArrayList<Target>());
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

    public Target targetIdentifiedBy(String targetId) {
        for(Target target : targets) {
            if (targetId.equals(target.id())) {
                return target;
            }
        }
        return null;
    }
}
