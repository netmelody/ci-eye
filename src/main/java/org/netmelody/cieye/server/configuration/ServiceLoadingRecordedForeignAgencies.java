package org.netmelody.cieye.server.configuration;

import static com.google.common.cache.CacheLoader.from;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.logging.Logbook;
import org.netmelody.cieye.core.observation.ObservationAgency;
import org.netmelody.cieye.server.ObservationAgencyFetcher;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public final class ServiceLoadingRecordedForeignAgencies implements ObservationAgencyFetcher, Refreshable {
    private final static Logbook LOGBOOK = LogKeeper.logbookFor(ServiceLoadingRecordedForeignAgencies.class);
    
    private final PluginDirectory pluginDirectory;
    private final AtomicReference<ServiceLoader<ObservationAgency>> services;

    private final LoadingCache<CiServerType, ObservationAgency> agencies =
            CacheBuilder.newBuilder().build(from(new Function<CiServerType, ObservationAgency>() {
                @Override
                public ObservationAgency apply(CiServerType type) {
                    return makeAgencyFor(type);
                }
            }));

    public ServiceLoadingRecordedForeignAgencies(PluginDirectory pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
        this.services = new AtomicReference<ServiceLoader<ObservationAgency>>(newServiceLoader());
    }

    @Override
    public ObservationAgency agencyFor(CiServerType type) {
        return agencies.getUnchecked(type);
    }

    @Override
    public void refresh() {
        if (pluginDirectory.updateAvailable()) {
            this.services.set(newServiceLoader());
            this.agencies.invalidateAll();
        }
    }

    private ObservationAgency makeAgencyFor(CiServerType type) {
        final String typeName = type.name();
        Iterator<ObservationAgency> agencies = available();
        
        while (agencies.hasNext()) {
            ObservationAgency agency = agencies.next();
            if (agency.canProvideSpyFor(type)) {
                return agency;
            }
        }
        
        throw new IllegalStateException("No CI Observation Module for " + typeName);
    }

    private ServiceLoader<ObservationAgency> newServiceLoader() {
        return ServiceLoader.load(ObservationAgency.class, pluginsClassLoader());
    }
    
    private Iterator<ObservationAgency> available() {
        return services.get().iterator();
    }

    private ClassLoader pluginsClassLoader() {
        Iterable<File> jarFiles = pluginDirectory.jars();
        return new URLClassLoader(FluentIterable.from(urlsOf(jarFiles)).toArray(URL.class));
    }

    private Set<URL> urlsOf(Iterable<File> jarFiles) {
        Set<URL> urls = Sets.newHashSet();
        Set<Throwable> problems = Sets.newHashSet();
        for (File file : jarFiles) {
            try {
                urls.add(file.toURI().toURL());
            } catch (MalformedURLException e) {
                problems.add(e);
            }
        }
        
        if (!problems.isEmpty()) {
            logProblems(problems);
        }
        
        return ImmutableSet.copyOf(urls);
    }

    private static void logProblems(Set<Throwable> problems) {
        for (Throwable throwable : problems) {
            LOGBOOK.error("Error loading plugin.", throwable);
        }
    }
}
