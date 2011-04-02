package org.netmelody.cieye.spies.teamcity;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.core.observation.ObservationAgency;

public final class TeamCityObservationAgency implements ObservationAgency {

    @Override
    public CiSpy provideSpyFor(Feature feature, CommunicationNetwork network, KnownOffendersDirectory directory) {
        return new TeamCityWitness(feature.endpoint(), network, directory);
    }
}