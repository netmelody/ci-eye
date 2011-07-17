package org.netmelody.cieye.core.observation;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.domain.TargetDetailGroup;

public interface CiSpy {

    TargetDigestGroup targetsConstituting(Feature feature);
    
    TargetDetailGroup statusOf(Feature feature);

    boolean takeNoteOf(String targetId, String note);
}
