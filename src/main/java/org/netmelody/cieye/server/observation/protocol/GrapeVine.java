package org.netmelody.cieye.server.observation.protocol;

public interface GrapeVine {

    boolean privileged();

    String doGet(String url);

    void doPost(String url);

    void doPut(String url, String content);

    void shutdown();

}