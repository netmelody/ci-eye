package org.netmelody.cii.witness.jenkins.jsondomain;

import org.netmelody.cii.domain.Status;

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