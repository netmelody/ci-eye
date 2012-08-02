package org.netmelody.cieye.spies.teamcity.jsondomain;

import java.util.ArrayList;
import java.util.List;

public final class Investigations {
    public List<Investigation> investigation;

    public List<Investigation> investigation() {
        return (null == investigation) ? new ArrayList<Investigation>() : investigation;
    }
}