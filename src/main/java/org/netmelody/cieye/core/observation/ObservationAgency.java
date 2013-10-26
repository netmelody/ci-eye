package org.netmelody.cieye.core.observation;

import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;

public interface ObservationAgency {
    boolean canProvideSpyFor(CiServerType type);
    CiSpy provideSpyFor(Feature feature, CommunicationNetwork network, KnownOffendersDirectory directory);
}
