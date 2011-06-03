package org.netmelody.cieye.server.configuration;

import java.io.File;

import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.server.ConfigurationFetcher;
import org.netmelody.cieye.server.LandscapeFetcher;
import org.netmelody.cieye.server.PictureFetcher;

public final class State implements PictureFetcher {

    private final SettingsInitialiser settings = new SettingsInitialiser();
    
    private final ConfigurationFetcher information = new ServerInformation(settings.settingsLocation());
    private final KnownOffendersDirectory detective = new RecordedKnownOffenders(settings.picturesFile());
    private final LandscapeFetcher targets = new RecordedObservationTargets(settings.viewsFile());

    public KnownOffendersDirectory detective() {
        return this.detective;
    }

    public LandscapeFetcher observationTargetDirectory() {
        return targets;
    }

    public ConfigurationFetcher serverInformation() {
        return information;
    }
    
    @Override
    public File getPictureResource(String name) {
        return new File(settings.picturesDirectory(), name);
    }
}
