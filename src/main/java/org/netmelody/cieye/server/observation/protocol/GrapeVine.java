package org.netmelody.cieye.server.observation.protocol;

public interface GrapeVine {

    String doGet(String url);

    void doPost(String url);

    void doPut(String url, String content);

    void shutdown();

}