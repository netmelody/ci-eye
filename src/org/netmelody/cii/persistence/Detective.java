package org.netmelody.cii.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.netmelody.cii.domain.User;

public final class Detective {

    public Map<String, User> userMap = new HashMap<String, User>();
    
    public Detective() {
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

    private void registerUser(String name, String pictureUrl, String... keywords) {
        final User user = new User(name, pictureUrl);
        userMap.put(name.toUpperCase(), user);
        for (String keyword : keywords) {
            userMap.put(keyword.toUpperCase(), user);
        }
    }
    
    public List<User> guiltyFrom(String changeText) {
        final List<User> guilty = new ArrayList<User>();
        
        final String upperChangeText = changeText.toUpperCase();
        for (String keyword : userMap.keySet()) {
            if (upperChangeText.contains(keyword)) {
                guilty.add(userMap.get(keyword));
            }
        }
        return guilty;
    }

}
