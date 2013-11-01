package org.netmelody.cieye.server;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetId;

public interface CiSpyIntermediary {
    TargetGroupBriefing briefingOn(Feature feature);
    boolean passNoteOn(Feature feature, TargetId targetId, String note);
}
