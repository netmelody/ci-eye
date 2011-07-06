package org.netmelody.cieye.server.response;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

import org.netmelody.cieye.core.domain.Sponsor;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.simpleframework.http.Request;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public final class CachedRequestOriginTracker implements RequestOriginTracker {
    
    private final KnownOffendersDirectory detective;
    
    private final Map<String, String> reverseLookup = new MapMaker().makeComputingMap(new Function<String, String>() {
        @Override
        public String apply(String ipAddress) {
            try {
                final InetAddress addr = InetAddress.getByName(ipAddress);
                return addr.getHostName();
            } catch (UnknownHostException e) {
                return ipAddress;
            }
        }
    });
    
    public CachedRequestOriginTracker(KnownOffendersDirectory detective) {
        this.detective = detective;
    }

    @Override
    public String originOf(Request request) {
        final String forwardedFor = request.getValue("X-Forwarded-For");
        
        if (null != forwardedFor && !forwardedFor.isEmpty()) {
            return reverseLookup.get(forwardedFor);
        }
        return request.getClientAddress().getHostName();
    }

    @Override
    public Set<Sponsor> sponsorsOf(Request request, String operation) {
        return detective.search(originOf(request) + " " + operation);
    }
}