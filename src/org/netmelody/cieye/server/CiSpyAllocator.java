package org.netmelody.cieye.server;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.observation.CiSpy;

public interface CiSpyAllocator {

    CiSpy spyFor(Feature feature);
}
