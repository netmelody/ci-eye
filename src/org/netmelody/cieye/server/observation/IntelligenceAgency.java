package org.netmelody.cieye.server.observation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.core.observation.ObservationAgency;
import org.netmelody.cieye.server.CiSpyAllocator;

public final class IntelligenceAgency implements CiSpyAllocator {

    private final Properties agencyConfiguration = new Properties();
    private final Map<String, CiSpy> witnesses = new HashMap<String, CiSpy>();
    
    private final CommunicationNetwork network;
    private final KnownOffendersDirectory directory;
    
    public IntelligenceAgency(CommunicationNetwork network, KnownOffendersDirectory directory) {
        this.network = network;
        this.directory = directory;
        try {
            agencyConfiguration.load(IntelligenceAgency.class.getResourceAsStream("CiObservationModules.properties"));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load CI Observation Modules");
        }
    }
    
    @Override
    public CiSpy spyFor(Feature feature) {
        if (!witnesses.containsKey(feature.endpoint())) {
            witnesses.put(feature.endpoint(), createSpyFor(feature));
        }
        return witnesses.get(feature.endpoint());
    }

    private CiSpy createSpyFor(Feature feature) {
        final String featureTypeName = feature.type().name();
        if (!agencyConfiguration.containsKey(featureTypeName)) {
            throw new IllegalStateException("No CI Observation Module for " + featureTypeName);
        }
        
        try {
            @SuppressWarnings("unchecked")
            final Class<? extends ObservationAgency> agencyClass =
                 (Class<? extends ObservationAgency>) Class.forName(agencyConfiguration.getProperty(featureTypeName));
            return new PollingSpy(agencyClass.newInstance().provideSpyFor(feature, network, directory));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load CI Observation Module for " + featureTypeName, e);
        }
    }
}
