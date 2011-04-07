package org.netmelody.cieye.server.response;

import org.simpleframework.http.Request;

public interface RequestOriginTracker {
    String originOf(Request request);
}