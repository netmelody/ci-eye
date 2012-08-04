package org.netmelody.cieye.core.domain;

import com.google.common.base.Preconditions;

public final class Feature {

    private final String name;
    private final String endpoint;
    private final CiServerType type;
    private final String username;
    private final String password; 
    
    public Feature(String name, String endpoint, CiServerType type) {
        this(name, endpoint, type, null, null);
    }

    public Feature(String name, String endpoint, CiServerType type, String username, String password) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(type);
        this.name = name;
        this.endpoint = endpoint;
        this.type = type;
        this.username = (null == username) ? "" : username;
        this.password = (null == password) ? "" : password;
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
    
    public String username() {
        return username;
    }
    
    public String password() {
        return password;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Feature)) {
            return false;
        }
        
        final Feature other = (Feature)obj;
        return other.name.equals(name)
                && other.endpoint.equals(endpoint)
                && other.type.equals(type)
                && other.username.equals(username)
                && other.password.equals(password);
    }
    
    @Override
    public int hashCode() {
        return 17 + name.hashCode() + endpoint.hashCode() * 7;
    }
}
