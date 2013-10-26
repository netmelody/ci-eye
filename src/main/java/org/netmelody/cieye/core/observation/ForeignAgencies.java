package org.netmelody.cieye.core.observation;

import org.netmelody.cieye.core.domain.CiServerType;

public interface ForeignAgencies {
    ObservationAgency agencyFor(CiServerType type);
    boolean hasChanged();
}