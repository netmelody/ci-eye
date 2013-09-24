package org.netmelody.cieye.core.domain;

import com.google.common.base.Preconditions;

public final class Feature {

    private final String name;
    private final String endpoint;
    private final CiServerType type;
    private final String username;
    private final String password;
    private final Flag showPersonalBuilds;

    public Feature(String name, String endpoint, CiServerType type) {
        this(name, endpoint, type, null, null, null);
    }

    public Feature(String name, String endpoint, CiServerType type, String showPersonalBuilds) {
        this(name, endpoint, type, null, null, showPersonalBuilds);
    }

    public Feature(String name, String endpoint, CiServerType type, String username, String password) {
        this(name, endpoint, type, username, password, null);
    }

    public Feature(String name, String endpoint, CiServerType type, String username, String password, String showPersonalBuilds) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(type);
        this.name = name;
        this.endpoint = endpoint;
        this.type = type;
        this.username = (null == username) ? "" : username;
        this.password = (null == password) ? "" : password;
        this.showPersonalBuilds = null == showPersonalBuilds ? Flag.ANY :
                showPersonalBuilds.equalsIgnoreCase("true") ? Flag.TRUE : Flag.FALSE;
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

    public Flag showPersonalBuilds() {
        return showPersonalBuilds;
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
                && other.password.equals(password)
                && other.showPersonalBuilds == showPersonalBuilds;
    }
    
    @Override
    public int hashCode() {
        return 17 + name.hashCode() + endpoint.hashCode() * 7;
    }
}
