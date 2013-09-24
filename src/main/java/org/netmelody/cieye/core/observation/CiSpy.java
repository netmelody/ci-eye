package org.netmelody.cieye.core.observation;

import org.netmelody.cieye.core.domain.*;

public interface CiSpy {

    TargetDigestGroup targetsConstituting(Feature feature);
    
    TargetDetail statusOf(TargetId target, Flag showPersonalBuilds);

    boolean takeNoteOf(TargetId target, String note);
}
