package org.netmelody.cieye.server.configuration;

import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.server.ConfigurationFetcher;
import org.netmelody.cieye.server.LandscapeFetcher;
import org.netmelody.cieye.server.PictureFetcher;

public final class ServerConfiguration {

    private final SettingsInitialiser settings = new SettingsInitialiser();
    
    private final ConfigurationFetcher information = new ServerInformation(settings.settingsLocation());
    private final KnownOffendersDirectory detective = new RecordedKnownOffenders(settings.picturesFile());
    private final LandscapeFetcher targets = new RecordedObservationTargets(settings.viewsFile());
    private final PictureFetcher album = new Album(settings.picturesDirectory());

    public KnownOffendersDirectory detective() {
        return this.detective;
    }

    public LandscapeFetcher observationTargetDirectory() {
        return targets;
    }

    public ConfigurationFetcher information() {
        return information;
    }
    
    public PictureFetcher album() {
        return album;
    }
}
