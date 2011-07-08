package org.netmelody.cieye.core.domain;

public final class CiServerType {
    
    private final String name;
    
    public CiServerType(String name) {
        this.name = name;
    }
    
    public String name() {
        return this.name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CiServerType)) {
            return false;
        }
        
        final CiServerType other = (CiServerType)obj;
        return other.name.equals(name);
    }
    
    @Override
    public int hashCode() {
        return 17 + name.hashCode();
    }
}
