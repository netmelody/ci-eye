package org.netmelody.cieye.server.observation;

import static com.google.common.cache.CacheLoader.from;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetId;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.ForeignAgencies;
import org.netmelody.cieye.core.observation.ForeignAgencies.RosterChangedEvent;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.server.CiSpyHandler;
import org.netmelody.cieye.server.CiSpyIntermediary;
import org.netmelody.cieye.server.TargetGroupBriefing;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.eventbus.Subscribe;

public final class IntelligenceAgency implements CiSpyIntermediary {

    public static IntelligenceAgency create(CommunicationNetwork network,
                                            KnownOffendersDirectory directory, 
                                            ForeignAgencies foreignAgencies) {
        IntelligenceAgency agency = new IntelligenceAgency(network, directory, foreignAgencies);
        foreignAgencies.registerInterestInChanges(agency);
        return agency;
    }

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
    
    private IntelligenceAgency(CommunicationNetwork network, KnownOffendersDirectory directory, ForeignAgencies foreignAgencies) {
        this.network = network;
        this.directory = directory;
        this.foreignAgencies = foreignAgencies;
    }
    
    private CiSpyHandler spyFor(Feature feature) {
        return handlers.getUnchecked(feature);
    }

    private CiSpyHandler createSpyFor(Feature feature) {
        final CiSpy spy = foreignAgencies.agencyFor(feature.type()).provideSpyFor(feature, network, directory);
        return new PollingSpyHandler(spy, feature);
    }

    @Override
    public TargetGroupBriefing briefingOn(Feature feature) {
        CiSpyHandler spy = spyFor(feature);
        return new TargetGroupBriefing(spy.statusOf(feature), spy.millisecondsUntilNextUpdate(feature));
    }

    @Override
    public boolean passNoteOn(Feature feature, TargetId targetId, String note) {
        CiSpyHandler spy = spyFor(feature);
        return spy.takeNoteOf(targetId, note);
    }

    @Subscribe 
    public void dismissCurrentSpies(RosterChangedEvent event) {
        for (CiSpyHandler spyHandler : handlers.asMap().values()) {
            spyHandler.endMission();
        }
        handlers.invalidateAll();
    }
}
