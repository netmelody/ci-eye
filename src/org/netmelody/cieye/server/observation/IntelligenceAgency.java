package org.netmelody.cieye.server.observation;

import java.util.Map;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.server.CiSpyAllocator;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public final class IntelligenceAgency implements CiSpyAllocator {

    private final ObservationAgencyConfiguration agencyConfiguration = new ObservationAgencyConfiguration();
    private final Map<Feature, CiSpy> witnesses = new MapMaker().makeComputingMap(new Function<Feature, CiSpy>() {
        @Override
        public CiSpy apply(Feature feature) {
            return createSpyFor(feature);
        }
    });
    
    private final CommunicationNetwork network;
    private final KnownOffendersDirectory directory;
    
    public IntelligenceAgency(CommunicationNetwork network, KnownOffendersDirectory directory) {
        this.network = network;
        this.directory = directory;
    }
    
    @Override
    public CiSpy spyFor(Feature feature) {
        return witnesses.get(feature);
    }

    private CiSpy createSpyFor(Feature feature) {
        final CiSpy spy = agencyConfiguration.agencyFor(feature.type()).provideSpyFor(feature, network, directory);
        return new PollingSpy(spy);
    }
}
