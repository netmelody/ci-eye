package org.netmelody.cii.persistence;

import org.netmelody.cii.domain.Landscape;
import org.netmelody.cii.domain.LandscapeGroup;

import com.google.common.collect.Lists;

public final class State {

    private LandscapeGroup landscapes = new LandscapeGroup(Lists.newArrayList(new Landscape("Ci-eye Demo")));

    public LandscapeGroup landscapes() {
        return this.landscapes;
    }

    public void addLandscape(Landscape landscape) {
        landscapes = landscapes.add(landscape);
    }

}
