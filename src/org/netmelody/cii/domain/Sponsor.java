package org.netmelody.cii.domain;

public final class Sponsor {

    private final String name;
    private final String picture;
    
    public Sponsor(String name) {
        this(name, name);
    }
    
    public Sponsor(String name, String picture) {
        this.name = name;
        this.picture = picture;
    }
    
    public String name() {
        return name;
    }
    
    public String picture() {
        return picture;
    }
}
