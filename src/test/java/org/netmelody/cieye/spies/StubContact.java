package org.netmelody.cieye.spies;

import java.util.Map;

import org.netmelody.cieye.core.observation.Contact;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import static com.google.common.collect.Maps.newHashMap;

public final class StubContact implements Contact {

    private final Map<String, String> responses = newHashMap();
    private final Gson gson = new Gson();

    public StubContact respondingWith(String url, String response) {
        responses.put(url, response);
        return this;
    }
    
    @Override
    public <T> T makeJsonRestCall(String url, Class<T> type) {
        return gson.fromJson(makeJsonRestCall(url), type);
    }

    @Override
    public JsonElement makeJsonRestCall(String url) {
        if (!responses.containsKey(url)) {
            throw new AssertionError("Unexpected request for " + url);
        }
        return new JsonParser().parse(responses.get(url));
    }

    @Override
    public void performBasicLogin(String loginUrl) {
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