package org.netmelody.cieye.server;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetDetailGroup;
import org.netmelody.cieye.core.domain.TargetId;

public interface CiSpyHandler {

    TargetDetailGroup statusOf(Feature feature);

    long millisecondsUntilNextUpdate(Feature feature);

    boolean takeNoteOf(TargetId targetId, String note);

    void endMission();
}
