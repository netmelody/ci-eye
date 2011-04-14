package org.netmelody.cieye.spies.teamcity.jsondomain;

import java.util.ArrayList;
import java.util.List;


public final class Builds {
    public int count;
    public List<Build> build;
    
    public List<Build> build() {
        return (null == build) ? new ArrayList<Build>() : build;
    }
}