package org.netmelody.cieye.spies.teamcity.jsondomain;

import java.util.ArrayList;
import java.util.List;

public final class TeamCityProjects {
    public List<Project> project;

    public List<Project> project() {
        return (null == project) ? new ArrayList<Project>() : project;
    }
}