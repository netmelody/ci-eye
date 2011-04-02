package org.netmelody.cieye.witness.jenkins.jsondomain;

import org.netmelody.cieye.core.domain.Status;

public class Job {
    public String name;
    public String url;
    public String color;
    
    public Status status() {
        if (null == color || color.startsWith("blue")) {
            return Status.GREEN;
        }
        if ("disabled".equals(color)) {
            return Status.DISABLED;
        }
        return Status.BROKEN;
    }
    
    public boolean building() {
        return (null != color && color.endsWith("_anime"));
    }
}