package org.netmelody.cieye.spies.jenkins.jsondomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ViewDetail extends View {
    public String description;
    public List<Job> jobs;
    
    public Collection<Job> jobs() {
        return (null != jobs) ? jobs : new ArrayList<Job>();
    }
}
