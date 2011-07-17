package org.netmelody.cieye.core.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class LandscapeObservation {

    private final List<TargetDetail> targets = new ArrayList<TargetDetail>();
    private final Set<Sponsor> dohGroup;
    
    public LandscapeObservation() {
        this(new TargetDetailGroup());
    }
    
    public LandscapeObservation(TargetDetailGroup targets) {
        this(newArrayList(targets.targets()), null);
    }
    
    private LandscapeObservation(Collection<TargetDetail> targets, Set<Sponsor> dohGroup) {
        this.targets.addAll(targets);
        this.dohGroup = (null == dohGroup) ? null : new HashSet<Sponsor>(dohGroup);
    }

    public LandscapeObservation add(TargetDetailGroup group) {
        if (null == group) {
            return this;
        }
        final LandscapeObservation result = new LandscapeObservation(targets, dohGroup);
        result.targets.addAll(newArrayList(group.targets()));
        return result;
    }

    public List<TargetDetail> targets() {
        return new ArrayList<TargetDetail>(targets);
    }
    
    public Set<Sponsor> dohGroup() {
        return (null == dohGroup) ? new HashSet<Sponsor>() : new HashSet<Sponsor>(dohGroup);
    }

    public LandscapeObservation withDoh(Set<Sponsor> dohGroup) {
        return new LandscapeObservation(this.targets, dohGroup);
    }
}
