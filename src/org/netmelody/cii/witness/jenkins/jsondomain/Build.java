package org.netmelody.cii.witness.jenkins.jsondomain;

import java.util.ArrayList;
import java.util.List;


public final class Build {
    public int number;
    public String url;
    public String description;
    public boolean building;
    public long duration;
    public String fullDisplayName;
    public String id;
    public boolean keepLog;
    public String result;
    public long timestamp;
    public String builtOn;
    public List<User> culprits;
    public List<Action> actions;
    public ChangeSet changeSet;
    //artifacts
    
    public List<String> upstreamBuildUrls() {
        final List<String> result = new ArrayList<String>();
        
        if (null != actions) {
            for (Action action : actions) {
                if (null != action) {
                    result.addAll(action.upstreamBuildUrls());
                }
            }
        }
        return result;
    }
}