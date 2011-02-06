package org.netmelody.cii.domain;

import java.util.ArrayList;
import java.util.List;

public final class Landscape {

    private final String name;
    private final List<Feature> features = new ArrayList<Feature>();
    
    public Landscape(String name) {
        this.name = name;
        this.features.add(new Feature(name + " Feature", "http://bob"));
    }
    
    public String name() {
        return name;
    }
}
