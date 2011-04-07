package org.netmelody.cieye.server.response;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.simpleframework.http.Request;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public class CachedRequestOriginTracker implements RequestOriginTracker {
    
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
    
    @Override
    public String originOf(Request request) {
        final String forwardedFor = request.getValue("X-Forwarded-For");
        
        if (null != forwardedFor && !forwardedFor.isEmpty()) {
            return reverseLookup.get(forwardedFor);
        }
        return request.getClientAddress().getHostName();
    }
}