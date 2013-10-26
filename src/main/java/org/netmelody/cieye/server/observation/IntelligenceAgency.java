package org.netmelody.cieye.server.observation;

import static com.google.common.cache.CacheLoader.from;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.ForeignAgencies;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.server.CiSpyAllocator;
import org.netmelody.cieye.server.CiSpyHandler;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public final class IntelligenceAgency implements CiSpyAllocator {

    private final LoadingCache<Feature, CiSpyHandler> handlers =
            CacheBuilder.newBuilder()
                .build(from(new Function<Feature, CiSpyHandler>() {
                @Override
                public CiSpyHandler apply(Feature feature) {
                    return createSpyFor(feature);
                }
            }));
    
    private final CommunicationNetwork network;
    private final KnownOffendersDirectory directory;
    private final ForeignAgencies foreignAgencies;
    
    public IntelligenceAgency(CommunicationNetwork network, KnownOffendersDirectory directory, ForeignAgencies foreignAgencies) {
        this.network = network;
        this.directory = directory;
        this.foreignAgencies = foreignAgencies;
    }
    
    @Override
    public CiSpyHandler spyFor(Feature feature) {
    	if (foreignAgencies.hasChanged()) {
    		recallSpies();
    	}
        return handlers.getUnchecked(feature);
    }

    private void recallSpies() {
        for (CiSpyHandler ciSpyHandler : handlers.asMap().values()) {
            ciSpyHandler.endMission();
        }
        handlers.invalidateAll();
    }

    private CiSpyHandler createSpyFor(Feature feature) {
        final CiSpy spy = foreignAgencies.agencyFor(feature.type()).provideSpyFor(feature, network, directory);
        return new PollingSpyHandler(spy, feature);
    }
}
