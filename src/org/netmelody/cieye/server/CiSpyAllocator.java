package org.netmelody.cieye.server;

import org.netmelody.cieye.core.domain.Feature;

public interface CiSpyAllocator {

    CiSpyHandler spyFor(Feature feature);
}
