package org.netmelody.cieye.server.configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.netmelody.cieye.core.observation.ForeignAgents;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.server.CiEyeServerInformationFetcher;
import org.netmelody.cieye.server.LandscapeFetcher;
import org.netmelody.cieye.server.PictureFetcher;

public final class ServerConfiguration {

    private final SettingsInitialiser settings = new SettingsInitialiser();
    
    private final ServerInformation information = new ServerInformation(settings.settingsLocation());
    private final RecordedKnownOffenders detective = new RecordedKnownOffenders(settings.picturesFile());
    private final RecordedObservationTargets targets = new RecordedObservationTargets(settings.viewsFile());
    private final RecordedForeignAgents foreignAgents = new RecordedForeignAgents(settings.pluginDirectory());
    private final Album album = new Album(settings.picturesDirectory());

    private static final class Refresher implements Runnable {
        private final Refreshable refreshable;
        public Refresher(Refreshable refreshable) {
            this.refreshable = refreshable;
        }
        @Override
        public void run() {
            refreshable.refresh();
        }
    }
    
    public ServerConfiguration() {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        executor.scheduleWithFixedDelay(new Refresher(detective), 1L, 10L, TimeUnit.SECONDS);
        executor.scheduleWithFixedDelay(new Refresher(targets), 1L, 10L, TimeUnit.SECONDS);
    }
    
    public KnownOffendersDirectory detective() {
        return this.detective;
    }

    public LandscapeFetcher observationTargetDirectory() {
        return targets;
    }

    public CiEyeServerInformationFetcher information() {
        return information;
    }
    
    public PictureFetcher album() {
        return album;
    }
    
    public ForeignAgents foreignAgents() {
        return foreignAgents;
    }
}
