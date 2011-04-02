package org.netmelody.cieye.core.domain;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Landscape {

    private final String name;
    private final List<Feature> features = new ArrayList<Feature>();
    
    public Landscape(String name, Feature... features) {
        this.name = name;
        
        if (null != features) {
            this.features.addAll(asList(features));
        }
    }
    
    public String name() {
        return name;
    }

    public Collection<Feature> features() {
        return new ArrayList<Feature>(features);
    }
}
