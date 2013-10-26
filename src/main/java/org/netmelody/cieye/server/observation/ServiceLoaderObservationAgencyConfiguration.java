package org.netmelody.cieye.server.observation;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.observation.ForeignAgents;
import org.netmelody.cieye.core.observation.ObservationAgency;

import com.google.common.collect.FluentIterable;


public class ServiceLoaderObservationAgencyConfiguration implements ObservationAgencyConfiguration {

    private final ClassLoader pluginsClassLoader;

    public ServiceLoaderObservationAgencyConfiguration(ForeignAgents foreignAgents) {
        this.pluginsClassLoader = new URLClassLoader(urls(foreignAgents.search()), Thread.currentThread().getContextClassLoader());
    }
    
    private URL[] urls(Set<ForeignAgents.CallingCard> search) {
        return FluentIterable.from(search).transform(ForeignAgents.CallingCard.TO_URL).toArray(URL.class);
    }

    @Override
    public ObservationAgency agencyFor(CiServerType type) {
        final String typeName = type.name();
        ServiceLoader<ObservationAgency> services = ServiceLoader.load(ObservationAgency.class, pluginsClassLoader);
        Iterator<ObservationAgency> iterator = services.iterator();
        
        while (iterator.hasNext()) {
            ObservationAgency agency = iterator.next();
            if (agency.canProvideSpyFor(type)) {
                return agency;
            }
        }
        
        throw new IllegalStateException("No CI Observation Module for " + typeName);
    }

}
