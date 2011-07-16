package org.netmelody.cieye.core.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class LandscapeObservation {

    private final List<Target> targets = new ArrayList<Target>();
    private final Set<Sponsor> dohGroup;
    
    public LandscapeObservation() {
        this(new ArrayList<Target>());
    }
    
    public LandscapeObservation(Collection<Target> targets) {
        this(targets, null);
    }
    
    private LandscapeObservation(Collection<Target> targets, Set<Sponsor> dohGroup) {
        this.targets.addAll(targets);
        this.dohGroup = (null == dohGroup) ? null : new HashSet<Sponsor>(dohGroup);
    }

    public LandscapeObservation add(LandscapeObservation group) {
        if (null == group) {
            return this;
        }
        final LandscapeObservation result = new LandscapeObservation(targets);
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

    public LandscapeObservation withDoh(Set<Sponsor> dohGroup) {
        return new LandscapeObservation(this.targets, dohGroup);
    }
}
