package org.netmelody.cieye.server.observation;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.server.CiSpyAllocator;
import org.netmelody.cieye.server.CiSpyHandler;

import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import static com.google.common.cache.CacheLoader.from;

public final class IntelligenceAgency implements CiSpyAllocator {

    private final ObservationAgencyConfiguration agencyConfiguration = new ObservationAgencyConfiguration();
    private final Cache<Feature, CiSpyHandler> handlers =
            CacheBuilder.newBuilder().build(from(new Function<Feature, CiSpyHandler>() {
                @Override
                public CiSpyHandler apply(Feature feature) {
                    return createSpyFor(feature);
                }
            }));
    
    private final CommunicationNetwork network;
    private final KnownOffendersDirectory directory;
    
    public IntelligenceAgency(CommunicationNetwork network, KnownOffendersDirectory directory) {
        this.network = network;
        this.directory = directory;
    }
    
    @Override
    public CiSpyHandler spyFor(Feature feature) {
        return handlers.getUnchecked(feature);
    }

    private CiSpyHandler createSpyFor(Feature feature) {
        final CiSpy spy = agencyConfiguration.agencyFor(feature.type()).provideSpyFor(feature, network, directory);
        return new PollingSpy(spy);
    }
}
