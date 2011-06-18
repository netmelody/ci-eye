package org.netmelody.cieye.server.observation;

import java.io.IOException;
import java.util.Properties;

import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.observation.ObservationAgency;

public final class ObservationAgencyConfiguration {
    private final Properties agencyConfiguration = new Properties();

    public ObservationAgencyConfiguration() {
        try {
            agencyConfiguration.load(ObservationAgencyConfiguration.class.getResourceAsStream("CiObservationModules.properties"));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load CI Observation Modules");
        }
    }
    
    public ObservationAgency agencyFor(CiServerType type) {
        final String typeName = type.name();
        if (!agencyConfiguration.containsKey(typeName)) {
            throw new IllegalStateException("No CI Observation Module for " + typeName);
        }
        
        try {
            @SuppressWarnings("unchecked")
            final Class<? extends ObservationAgency> agencyClass =
                 (Class<? extends ObservationAgency>) Class.forName(agencyConfiguration.getProperty(typeName));
            return agencyClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load CI Observation Module for " + typeName, e);
        }
    }
}