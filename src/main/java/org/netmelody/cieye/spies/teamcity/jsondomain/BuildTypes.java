package org.netmelody.cieye.spies.teamcity.jsondomain;

import java.util.ArrayList;
import java.util.List;

public final class BuildTypes {
    public List<BuildType> buildType;

    public List<BuildType> buildType() {
        return (null == buildType) ? new ArrayList<BuildType>() : buildType;
    }
}