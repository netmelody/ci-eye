package org.netmelody.cii.domain;

import static com.google.common.collect.Lists.newArrayList;
import static org.netmelody.cii.domain.CiServerType.DEMO;

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

    public static LandscapeGroup demo() {
        return new LandscapeGroup(newArrayList(new Landscape("Ci-eye Demo", new Feature("My Product", "", DEMO))));
    }
}
