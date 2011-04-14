package org.netmelody.cieye.spies.jenkins.jsondomain;

import java.util.ArrayList;
import java.util.List;


public final class Server {
    public List<Label> assignedLabels;
    public String mode;
    public String nodeName;
    public String nodeDescription;
    public int numExecutors;
    public String description;
    public List<Job> jobs;
    public Load overallLoad;
    public View primaryView;
    public int slaveAgentPort;
    public boolean useCrumbs;
    public boolean useSecurity;
    public List<View> views;
    
    public List<View> views() {
        return (views == null) ? new ArrayList<View>() : views;
    }
}