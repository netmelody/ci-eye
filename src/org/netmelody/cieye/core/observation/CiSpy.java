package org.netmelody.cieye.core.observation;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.domain.TargetId;

public interface CiSpy {

    TargetDigestGroup targetsConstituting(Feature feature);
    
    TargetDetail statusOf(TargetId target);

    boolean takeNoteOf(TargetId target, String note);
}
