package org.netmelody.cii.domain;

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

}
