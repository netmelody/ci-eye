package org.netmelody.cieye.core.domain;

import java.util.HashMap;
import java.util.Map;

public class CiServerType {
    
    private static final Map<String, CiServerType> aliases = new HashMap<String, CiServerType>();
    static {
        aliases.put("DEMO", new CiServerType("DEMO"));
        aliases.put("JENKINS", new CiServerType("JENKINS"));
        aliases.put("HUDSON", new CiServerType("JENKINS"));
        aliases.put("TEAMCITY", new CiServerType("TEAMCITY"));
    }
    
    private final String name;
    
    public CiServerType(String name) {
        this.name = name;
    }
    
    public static CiServerType from(String name) {
        if (aliases.containsKey(name.toUpperCase())) {
            return aliases.get(name.toUpperCase());
        }
        return aliases.get("DEMO");
    }
    
    public String name() {
        return this.name;
    }
}
