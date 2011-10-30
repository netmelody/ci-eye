package org.netmelody.cieye.server.response;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import org.netmelody.cieye.core.domain.Sponsor;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.simpleframework.http.Request;

import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import static com.google.common.cache.CacheLoader.from;

public final class CachedRequestOriginTracker implements RequestOriginTracker {
    
    private final KnownOffendersDirectory detective;
    
    private final Cache<String, String> reverseLookup =
            CacheBuilder.newBuilder().build(from(new Function<String, String>() {
                @Override
                public String apply(String ipAddress) {
                    try {
                        final InetAddress addr = InetAddress.getByName(ipAddress);
                        return addr.getHostName();
                    } catch (UnknownHostException e) {
                        return ipAddress;
                    }
                }
            }));
    
    public CachedRequestOriginTracker(KnownOffendersDirectory detective) {
        this.detective = detective;
    }

    @Override
    public String originOf(Request request) {
        final String forwardedFor = request.getValue("X-Forwarded-For");
        
        if (null != forwardedFor && !forwardedFor.isEmpty()) {
            return reverseLookup.getUnchecked(forwardedFor);
        }
        return request.getClientAddress().getHostName();
    }

    @Override
    public Set<Sponsor> sponsorsOf(Request request, String operation) {
        return detective.search(originOf(request) + " " + operation);
    }
}