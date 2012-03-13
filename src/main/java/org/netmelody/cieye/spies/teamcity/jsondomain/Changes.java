package org.netmelody.cieye.spies.teamcity.jsondomain;

import java.util.ArrayList;
import java.util.List;


public final class Changes {
    //String @count
    public List<Change> change;

    public List<Change> change() {
        return (null == change) ? new ArrayList<Change>() : change;
    }
}