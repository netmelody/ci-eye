package org.netmelody.cieye.core.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class LandscapeGroup {

    private final List<Landscape> landscapes = new ArrayList<Landscape>();
    
    public LandscapeGroup() {
        this(new ArrayList<Landscape>());
    }
    
    public LandscapeGroup(Collection<Landscape> landscapes) {
        this.landscapes.addAll(landscapes);
    }

    public LandscapeGroup add(Landscape landscape) {
        final List<Landscape> newLandscapes = new ArrayList<Landscape>(landscapes);
        newLandscapes.add(landscape);
        return new LandscapeGroup(newLandscapes);
    }
    
    public Landscape landscapeNamed(String name) {
        for (Landscape landscape : landscapes) {
            if (name.equals(landscape.name())) {
                return landscape;
            }
        }
        return new Landscape("name");
    }
}
