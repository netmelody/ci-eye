package org.netmelody.cieye.core.domain;


public final class TargetId {

    private final String id;
    
    public TargetId() {
        this("");
    }

    public TargetId(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }
}
