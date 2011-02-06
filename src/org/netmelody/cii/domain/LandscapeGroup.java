package org.netmelody.cii.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class LandscapeGroup {

    private final List<Landscape> landscapes = new ArrayList<Landscape>();
    
    public LandscapeGroup(Collection<Landscape> landscapes) {
        this.landscapes.addAll(landscapes);
    }

    public LandscapeGroup add(Landscape landscape) {
        final LandscapeGroup result = new LandscapeGroup(landscapes);
        result.landscapes.add(landscape);
        return result;
    }
    
    public Landscape landscapeNamed(String name) {
        for (Landscape landscape : landscapes) {
            if (name.equals(landscape.name())) {
                return landscape;
            }
        }
        return null;
    }
}
