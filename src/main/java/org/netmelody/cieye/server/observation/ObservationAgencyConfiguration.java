package org.netmelody.cieye.server.observation;

import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.observation.ObservationAgency;

public interface ObservationAgencyConfiguration {

    ObservationAgency agencyFor(CiServerType type);

}