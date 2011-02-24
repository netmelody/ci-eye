package org.netmelody.cii.persistence;

import org.netmelody.cii.domain.Landscape;
import org.netmelody.cii.domain.LandscapeGroup;

public final class State {

    private LandscapeGroup landscapes;

    public State() {
        SettingsInitialiser settings = new SettingsInitialiser();
        landscapes = ViewsRepository.readLandscapesFrom(settings.viewsFile());
    }
    
    public LandscapeGroup landscapes() {
        return this.landscapes;
    }
    
    public Detective detective() {
        return new Detective();
    }

    public void addLandscape(Landscape landscape) {
        landscapes = landscapes.add(landscape);
    }

    public Landscape landscapeNamed(String name) {
        return landscapes.landscapeNamed(name);
    }
}
