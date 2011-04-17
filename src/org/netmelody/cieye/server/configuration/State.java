package org.netmelody.cieye.server.configuration;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.jar.Manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.LandscapeGroup;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.server.ConfigurationFetcher;
import org.netmelody.cieye.server.LandscapeFetcher;
import org.netmelody.cieye.server.PictureFetcher;

public final class State implements LandscapeFetcher, PictureFetcher, ConfigurationFetcher {

    private static final Log LOG = LogFactory.getLog(State.class);
    
    private final SettingsInitialiser settings = new SettingsInitialiser();
    private final KnownOffendersDirectory detective = new RecordedKnownOffenders(settings.picturesFile());
    private LandscapeGroup landscapes = new ViewsRepository(settings.viewsFile()).landscapes();

    @Override
    public LandscapeGroup landscapes() {
        return this.landscapes;
    }
    
    public KnownOffendersDirectory detective() {
        return this.detective;
    }

    @Override
    public Landscape landscapeNamed(String name) {
        return landscapes.landscapeNamed(name);
    }

    @Override
    public File getPictureResource(String name) {
        return settings.pictureNamed(name);
    }

    @Override
    public String settingsLocation() {
        try {
            return new StringBuilder()
                            .append(InetAddress.getLocalHost().getHostName())
                            .append(" (").append(InetAddress.getLocalHost().getHostAddress()).append(")")
                            .append(" ")
                            .append(settings.settingsLocation())
                            .toString();
        }
        catch (Exception e) {
            return "";
        }
    }
    
    public String getVersion() {
        String result = "";
        try {
            final Manifest manifest = new Manifest(State.class.getResourceAsStream("/META-INF/MANIFEST.MF"));
            result = manifest.getMainAttributes().getValue("Implementation-Version");
        } catch (IOException e) {
            LOG.warn("Failed to read Implementation-Version", e);
        }
        return result;
    }
}
