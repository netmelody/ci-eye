package org.netmelody.cieye.spies.jenkins.jsondomain;

import java.util.List;


public final class Computer {
    public String displayName;
    public String icon;
    public boolean idle;
    public boolean jnlpAgent;
    public boolean launchSupported;
    public boolean manualLaunchAllowed;
    public List<Action> actions;
    public int numExecutors;
    public boolean offline;
    public boolean temporarilyOffline;
    //oneOffExecutors
    //offlineCause
    //loadStatistics
    //executors
    //monitorData
}