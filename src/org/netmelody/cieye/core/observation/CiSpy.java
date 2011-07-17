package org.netmelody.cieye.core.observation;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.domain.TargetDetailGroup;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.domain.TargetId;

public interface CiSpy {

    TargetDigestGroup targetsConstituting(Feature feature);
    
    TargetDetailGroup statusOf(Feature feature);
    
    TargetDetail statusOf(TargetId target);

    boolean takeNoteOf(String targetId, String note);
}
