package org.netmelody.cieye.core.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class TargetDigestGroup {

    private final List<TargetDigest> targets = new ArrayList<TargetDigest>();
    
    public TargetDigestGroup() {
        this(new ArrayList<TargetDigest>());
    }
    
    public TargetDigestGroup(Collection<TargetDigest> targets) {
        this.targets.addAll(targets);
    }
    
    public TargetDigestGroup add(TargetDigestGroup group) {
        if (null == group) {
            return this;
        }
        final TargetDigestGroup result = new TargetDigestGroup(targets);
        result.targets.addAll(group.targets);
        return result;
    }

    public List<TargetDigest> targets() {
        return new ArrayList<TargetDigest>(targets);
    }
}
