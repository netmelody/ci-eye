package org.netmelody.cieye.server;

import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.LandscapeGroup;

public interface LandscapeFetcher {

    LandscapeGroup landscapes();

    Landscape landscapeNamed(String name);

}