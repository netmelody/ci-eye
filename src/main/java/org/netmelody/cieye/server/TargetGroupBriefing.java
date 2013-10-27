package org.netmelody.cieye.server;

import org.netmelody.cieye.core.domain.TargetDetailGroup;

public final class TargetGroupBriefing {

    public final TargetDetailGroup status;
    public final long millisecondsUntilNextUpdate;
    
    public TargetGroupBriefing(TargetDetailGroup targetDetailGroup, long millisecondsUntilNextUpdate) {
        this.status = targetDetailGroup;
        this.millisecondsUntilNextUpdate = millisecondsUntilNextUpdate;
    }
    
}
