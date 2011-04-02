package org.netmelody.cieye.core.observation;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetGroup;

public interface CiSpy {

    TargetGroup statusOf(Feature feature);

    long millisecondsUntilNextUpdate(Feature feature);

    boolean takeNoteOf(String targetId, String note);
}
