package org.netmelody.cieye.core.domain;

import java.util.HashMap;
import java.util.Map;

public enum CiServerType {
    DEMO, JENKINS, TEAMCITY;

    private static final Map<String, CiServerType> aliases = new HashMap<String, CiServerType>();
    static {
        aliases.put("DEMO", DEMO);
        aliases.put("JENKINS", JENKINS);
        aliases.put("HUDSON", JENKINS);
        aliases.put("TEAMCITY", TEAMCITY);
    }
    
    public static CiServerType from(String name) {
        if (aliases.containsKey(name.toUpperCase())) {
            return aliases.get(name.toUpperCase());
        }
        return DEMO;
    }
}
