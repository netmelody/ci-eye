package org.netmelody.cieye.spies.jenkins.jsondomain;

import java.util.ArrayList;
import java.util.List;

import org.netmelody.cieye.core.domain.Status;

public final class BuildDetail extends Build {
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
    
    public Status status() {
        if (!building) {
            return ("SUCCESS".equals(result)) ? Status.GREEN : Status.BROKEN;
        }
        
        if (null != actions) {
            for (Action action : actions) {
                if (null != action) {
                    if (action.failCount > 0) {
                        return Status.BROKEN;
                    }
                }
            }
        }
        return Status.UNKNOWN;
    }
}
