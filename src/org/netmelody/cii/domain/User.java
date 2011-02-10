package org.netmelody.cii.domain;

public final class User {

    private final String name;
    private final String picture;
    
    public User(String name) {
        this(name, name);
    }
    
    public User(String name, String picture) {
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
