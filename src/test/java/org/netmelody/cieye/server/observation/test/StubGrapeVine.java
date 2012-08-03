package org.netmelody.cieye.server.observation.test;

import java.util.Map;

import org.netmelody.cieye.server.observation.protocol.GrapeVine;

import static com.google.common.collect.Maps.newHashMap;

public final class StubGrapeVine implements GrapeVine {

    private final Map<String, String> responses = newHashMap();

    public StubGrapeVine respondingWith(String url, String response) {
        responses.put(url, response);
        return this;
    }
    
    @Override
    public String doGet(String url) {
        if (!responses.containsKey(url)) {
            throw new AssertionError("Unexpected request for " + url);
        }
        return responses.get(url);
    }

    @Override
    public boolean privileged() {
        return false;
    }

    @Override
    public void doPost(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doPut(String url, String content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }
}
