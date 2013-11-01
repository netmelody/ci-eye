package org.netmelody.cieye.server;

import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.observation.ObservationAgency;

public interface ObservationAgencyFetcher {
    ObservationAgency agencyFor(CiServerType type);
}