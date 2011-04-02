package org.netmelody.cieye.core.observation;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.persistence.Detective;

public interface ObservationAgency {
    
    CiSpy provideSpyFor(Feature feature, Detective mostWantedDirectory);
}
