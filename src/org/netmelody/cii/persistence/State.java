package org.netmelody.cii.persistence;

import static com.google.common.collect.Lists.newArrayList;
import static org.netmelody.cii.domain.CiServerType.DEMO;
import static org.netmelody.cii.domain.CiServerType.JENKINS;

import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.Landscape;
import org.netmelody.cii.domain.LandscapeGroup;

public final class State {

    private LandscapeGroup landscapes =
        new LandscapeGroup(newArrayList(new Landscape("Ci-eye Demo", new Feature("My Product", "", DEMO)),
                                        new Landscape("HIP", new Feature("HIP Hawk", "http://ccmain:8080", JENKINS))));

    public LandscapeGroup landscapes() {
        return this.landscapes;
    }

    public void addLandscape(Landscape landscape) {
        landscapes = landscapes.add(landscape);
    }

    public Landscape landscapeNamed(String name) {
        return landscapes.landscapeNamed(name);
    }
}
