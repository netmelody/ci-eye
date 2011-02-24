package org.netmelody.cii.persistence;

import org.netmelody.cii.domain.Landscape;
import org.netmelody.cii.domain.LandscapeGroup;

public final class State {

    private final Detective detective;
    private LandscapeGroup landscapes;

    public State() {
        SettingsInitialiser settings = new SettingsInitialiser();
        this.landscapes = new ViewsRepository(settings.viewsFile()).landscapes();
        this.detective = new Detective(settings.picturesFile());
    }
    
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
}
