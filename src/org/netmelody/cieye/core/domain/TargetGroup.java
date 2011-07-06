package org.netmelody.cieye.core.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TargetGroup {

    private final List<Target> targets = new ArrayList<Target>();
    private final Set<Sponsor> dohGroup;
    
    public TargetGroup() {
        this(new ArrayList<Target>());
    }
    
    public TargetGroup(Collection<Target> targets) {
        this(targets, null);
    }
    
    private TargetGroup(Collection<Target> targets, Set<Sponsor> dohGroup) {
        this.targets.addAll(targets);
        this.dohGroup = (null == dohGroup) ? null : new HashSet<Sponsor>(dohGroup);
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
    
    public Set<Sponsor> dohGroup() {
        return (null == dohGroup) ? new HashSet<Sponsor>() : new HashSet<Sponsor>(dohGroup);
    }

    public Target targetIdentifiedBy(String targetId) {
        for(Target target : targets) {
            if (targetId.equals(target.id())) {
                return target;
            }
        }
        return null;
    }

    public TargetGroup withDoh(Set<Sponsor> dohGroup) {
        return new TargetGroup(this.targets, dohGroup);
    }
}
