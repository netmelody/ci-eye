package org.netmelody.cii.domain;

public final class Feature {

    private final String name;
    private final String endpoint;
    
    public Feature(String name) {
        this(name, name);
    }
    
    public Feature(String name, String endpoint) {
        this.name = name;
        this.endpoint = endpoint;
    }
    
    public String name() {
        return name;
    }
    
    public String endpoint() {
        return endpoint;
    }
}
