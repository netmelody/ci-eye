package org.netmelody.cieye.spies;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.spies.teamcity.test.TeamCityCommunicatorTest;

import com.google.gson.Gson;

import static com.google.common.collect.Maps.newHashMap;

public final class StubContact implements Contact {

    private final Map<String, InputStream> responses = newHashMap();
    private final Gson gson = new Gson();
    private final Class<?> owningClass;
    
    public StubContact(Class<TeamCityCommunicatorTest> owningClass) {
        this.owningClass = owningClass;
    }

    public StubContact respondingWith(String url, String responseFileName) {
        responses.put(url, owningClass.getResourceAsStream(responseFileName));
        return this;
    }
    
    @Override
    public <T> T makeJsonRestCall(String url, Class<T> type) {
        if (!responses.containsKey(url)) {
            throw new AssertionError("Unexpected request for " + url);
        }
        return gson.fromJson(new InputStreamReader(responses.get(url)), type);
    }

    @Override
    public void performBasicLogin(String loginUrl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void performBasicAuthentication(String username, String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doPost(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doPut(String url, String content) {
        throw new UnsupportedOperationException();
    }
}