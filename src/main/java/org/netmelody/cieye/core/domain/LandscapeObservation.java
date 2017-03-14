package org.netmelody.cieye.core.domain;

import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;

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

    public LandscapeObservation forSponsor(final Sponsor sponsor) {
        return new LandscapeObservation(onSponsor(sponsor), dohGroup);
    }

    private Collection<TargetDetail> onSponsor(final Sponsor sponsor) {
        return filter(targets, new Predicate<TargetDetail>() {
            @Override
            public boolean apply(TargetDetail input) {
                return input.sponsors().contains(sponsor);
            }
        });
    }
}
