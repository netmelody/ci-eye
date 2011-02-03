package org.netmelody.cii.domain;

public final class Target {

    private final String name;
    private final Status status; 
    
    public Target(String name) {
        this(name, Status.GREEN);
    }

    public Target(String name, Status status) {
        this.name = name;
        this.status = status;
    }

    public String name() {
        return name;
    }
    
    public Status status() {
        return status;
    }
}
