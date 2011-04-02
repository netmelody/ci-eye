package org.netmelody.cieye.witness;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetGroup;

public interface Witness {

    TargetGroup statusOf(Feature feature);

    long millisecondsUntilNextUpdate(Feature feature);

    boolean takeNoteOf(String targetId, String note);
}
