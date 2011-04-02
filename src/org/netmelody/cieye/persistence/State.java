package org.netmelody.cieye.persistence;

import java.io.File;
import java.net.InetAddress;

import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.LandscapeGroup;
import org.netmelody.cieye.core.observation.Detective;

public final class State {

    private final SettingsInitialiser settings = new SettingsInitialiser();
    private final Detective detective = new Detective(settings.picturesFile());
    private LandscapeGroup landscapes = new ViewsRepository(settings.viewsFile()).landscapes();

    public LandscapeGroup landscapes() {
        return this.landscapes;
    }
    
    public Detective detective() {
        return this.detective;
    }

    public void addLandscape(Landscape landscape) {
        landscapes = landscapes.add(landscape);
    }

    public Landscape landscapeNamed(String name) {
        return landscapes.landscapeNamed(name);
    }

    public File getPictureResource(String name) {
        return settings.pictureNamed(name);
    }

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
}
