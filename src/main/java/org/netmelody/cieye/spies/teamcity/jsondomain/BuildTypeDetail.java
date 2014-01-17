package org.netmelody.cieye.spies.teamcity.jsondomain;


public final class BuildTypeDetail extends BuildType {
    public String description;
    public boolean paused;
    public Project project;
    public BuildsHref builds;
    public BuildSettings settings;

    public boolean externalStatusDisabled() {
        return settings.externalStatusDisabled();
    }
    //vcs-root
    //parameters
    //runParameters
}