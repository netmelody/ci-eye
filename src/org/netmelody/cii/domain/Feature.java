package org.netmelody.cii.domain;

public final class Feature {

    private final String name;
    private final String endpoint;
    private final CiServerType type; 
    
    public Feature(String name) {
        this(name, name);
    }
    
    public Feature(String name, String endpoint) {
        this(name, endpoint, CiServerType.DEMO);
    }
    
    public Feature(String name, String endpoint, CiServerType type) {
        this.name = name;
        this.endpoint = endpoint;
        this.type = type;
    }
    
    public String name() {
        return name;
    }
    
    public String endpoint() {
        return endpoint;
    }
    
    public CiServerType type() {
        return type;
    }
}
