package org.netmelody.cieye.server.configuration;

import java.util.ResourceBundle;

import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.observation.ForeignAgencies;
import org.netmelody.cieye.core.observation.ObservationAgency;

public final class ResourceBundleObservationAgencyConfiguration implements ForeignAgencies {
    
    private static final ResourceBundle AGENCY_CONFIGURATION = ResourceBundle.getBundle(ResourceBundleObservationAgencyConfiguration.class.getName());

    @Override
    public ObservationAgency agencyFor(CiServerType type) {
        final String typeName = type.name();
        if (!AGENCY_CONFIGURATION.containsKey(typeName)) {
            throw new IllegalStateException("No CI Observation Module for " + typeName);
        }
        
        try {
            @SuppressWarnings("unchecked")
            final Class<? extends ObservationAgency> agencyClass =
                 (Class<? extends ObservationAgency>) Class.forName(AGENCY_CONFIGURATION.getString(typeName));
            return agencyClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load CI Observation Module for " + typeName, e);
        }
    }

    @Override
    public void registerInterestInChanges(Object interested) {
        // No-op, never changes
    }

}