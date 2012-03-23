package org.netmelody.cieye.server.response;

import java.util.Set;

import org.netmelody.cieye.core.domain.Sponsor;
import org.simpleframework.http.Request;

public interface RequestOriginTracker {
    
    String originOf(Request request);
    
    Set<Sponsor> sponsorsOf(Request request, String operation);

    Sponsor sponsorWith(String fingerprint);
}