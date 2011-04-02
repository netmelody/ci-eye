package org.netmelody.cieye.core.observation;

import org.netmelody.cieye.core.domain.Feature;

public interface ObservationAgency {
    
    CiSpy provideSpyFor(Feature feature, CommunicationNetwork network, KnownOffendersDirectory directory);
}
