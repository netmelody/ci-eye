package org.netmelody.cieye.server.response;

import java.util.List;

import org.netmelody.cieye.core.domain.Sponsor;
import org.simpleframework.http.Request;

public interface RequestOriginTracker {
    
    String originOf(Request request);
    
    List<Sponsor> sponsorsOf(Request request, String operation);
}