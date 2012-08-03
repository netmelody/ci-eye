package org.netmelody.cieye.core.observation;

import com.google.gson.JsonElement;

public interface Contact {

    <T> T makeJsonRestCall(String url, Class<T> type);

    JsonElement makeJsonRestCall(String url);

    boolean privileged();

    void doPost(String url);

    void doPut(String url, String content);
}