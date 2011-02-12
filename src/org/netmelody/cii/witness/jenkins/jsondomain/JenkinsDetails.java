package org.netmelody.cii.witness.jenkins.jsondomain;

import java.util.List;


public final class JenkinsDetails {
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
}