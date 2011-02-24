package org.netmelody.cii.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.netmelody.cii.domain.Sponsor;

public final class Detective {

    public Map<String, Sponsor> userMap = new HashMap<String, Sponsor>();
    
    private Detective() {
        registerUser("Ryan Alexander", "http://teamcity-server:8111/img/staffpics/ryan.jpg", "ralexander", "ryan");
        registerUser("Graham Allan", "http://teamcity-server:8111/img/staffpics/graham.jpg", "gallan", "graham");
        registerUser("Andrew Booker", "http://teamcity-server:8111/img/staffpics/andyb.jpg", "andrew", "abooker", "andrewb", "andyb");
        registerUser("Tom Denley", "http://teamcity-server:8111/img/staffpics/tomd.jpg", "tomd", "tdenley");
        registerUser("Dominic Fox", "http://teamcity-server:8111/img/staffpics/dfox.jpg", "dfox", "dominic", "dominicf");
        registerUser("Andy Parker", "http://teamcity-server:8111/img/staffpics/andyp.jpg", "aparker", "andy", "andyp", "andrewp");
        registerUser("Paulo Schneider", "http://teamcity-server:8111/img/staffpics/paulo.jpg", "pschneider", "paulo");
        registerUser("Samir Talwar", "http://teamcity-server:8111/img/staffpics/samir.jpg", "stalwar", "samir");
        registerUser("Tony Tsui", "http://teamcity-server:8111/img/staffpics/tony.jpg", "ttsui", "tony");
        registerUser("Tom Westmacott", "http://teamcity-server:8111/img/staffpics/tomw.jpg", "tomw", "twestmacott");
        registerUser("Wendy Yip", "http://teamcity-server:8111/img/staffpics/wendy.jpg", "wendy", "wyip");
    }

    public Detective(File picturesFile) {
        this();
    }

    private void registerUser(String name, String pictureUrl, String... keywords) {
        final Sponsor user = new Sponsor(name, pictureUrl);
        userMap.put(name.toUpperCase(), user);
        for (String keyword : keywords) {
            userMap.put(keyword.toUpperCase(), user);
        }
    }
    
    public List<Sponsor> sponsorsOf(String changeText) {
        final Collection<Sponsor> sponsors = new HashSet<Sponsor>();
        
        final String upperChangeText = changeText.toUpperCase();
        for (String keyword : userMap.keySet()) {
            if (upperChangeText.contains(keyword)) {
                sponsors.add(userMap.get(keyword));
            }
        }
        return new ArrayList<Sponsor>(sponsors);
    }

}
