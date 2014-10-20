package org.netmelody.cieye.server.observation;

import static com.google.common.cache.CacheLoader.from;

import java.util.Map;

import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetId;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.core.observation.ObservationAgency;
import org.netmelody.cieye.server.CiSpyIntermediary;
import org.netmelody.cieye.server.ObservationAgencyFetcher;
import org.netmelody.cieye.server.TargetGroupBriefing;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Maps;

public final class IntelligenceAgency implements CiSpyIntermediary {

    public static IntelligenceAgency create(CommunicationNetwork network,
                                            KnownOffendersDirectory directory, 
                                            ObservationAgencyFetcher foreignAgencies) {
        IntelligenceAgency agency = new IntelligenceAgency(network, directory, foreignAgencies);
        return agency;
    }

    private final Map<CiServerType, ObservationAgency> agencies = Maps.newHashMap();

    private final LoadingCache<Feature, PollingSpyHandler> handlers =
            CacheBuilder.newBuilder().removalListener(new RemovalListener<Feature, PollingSpyHandler>() {
                @Override
                public void onRemoval(RemovalNotification<Feature, PollingSpyHandler> notification) {
                    notification.getValue().endMission();
                }
            }).build(from(new Function<Feature, PollingSpyHandler>() {
                @Override
                public PollingSpyHandler apply(Feature feature) {
                    return createSpyFor(feature);
                }
            }));

    private final CommunicationNetwork network;
    private final KnownOffendersDirectory directory;
    private final ObservationAgencyFetcher foreignAgencies;

    private IntelligenceAgency(CommunicationNetwork network, KnownOffendersDirectory directory, ObservationAgencyFetcher foreignAgencies) {
        this.network = network;
        this.directory = directory;
        this.foreignAgencies = foreignAgencies;
    }

    private PollingSpyHandler spyFor(Feature feature) {
        ObservationAgency latestAgency = foreignAgencies.agencyFor(feature.type());
        ObservationAgency currentAgency = agencies.get(feature.type());

        if (currentAgency != latestAgency) {
            agencies.put(feature.type(), latestAgency);
            handlers.invalidate(feature);
        }

        return handlers.getUnchecked(feature);
    }

    private PollingSpyHandler createSpyFor(Feature feature) {
        final ObservationAgency agency = foreignAgencies.agencyFor(feature.type());
        final CiSpy spy = agency.provideSpyFor(feature, network, directory);
        return new PollingSpyHandler(spy, feature);
    }

    @Override
    public TargetGroupBriefing briefingOn(Feature feature) {
        PollingSpyHandler spy = spyFor(feature);
        return new TargetGroupBriefing(spy.statusOf(feature), spy.millisecondsUntilNextUpdate(feature));
    }

    @Override
    public boolean passNoteOn(Feature feature, TargetId targetId, String note) {
        PollingSpyHandler spy = spyFor(feature);
        return spy.takeNoteOf(targetId, note);
    }
}
