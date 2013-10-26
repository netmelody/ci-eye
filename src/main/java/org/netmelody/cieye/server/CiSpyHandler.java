package org.netmelody.cieye.server;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetDetailGroup;

public interface CiSpyHandler {

    TargetDetailGroup statusOf(Feature feature);

    long millisecondsUntilNextUpdate(Feature feature);

    boolean takeNoteOf(String targetId, String note);

    void endMission();
}
