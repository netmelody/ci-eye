package org.netmelody.cieye.server.response;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.server.LandscapeFetcher;
import org.netmelody.cieye.server.CiSpyAllocator;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.resource.Resource;

public final class TargetNotationHandler implements Resource {

    private static final Log LOG = LogFactory.getLog(TargetNotationHandler.class);
    
    private final LandscapeFetcher state;
    private final CiSpyAllocator witnessProvider;

    public TargetNotationHandler(LandscapeFetcher state, CiSpyAllocator witnessProvider) {
        this.state = state;
        this.witnessProvider = witnessProvider;
    }

    @Override
    public void handle(Request request, Response response) {
        try {
            final String targetId = request.getForm().get("id");
            final String note = request.getForm().get("note") + " by " + getOriginator(request);
            
            final String[] segments = request.getAddress().getPath().getSegments();
            final Landscape landscape = state.landscapeNamed(segments[segments.length - 2]);
            
            for (Feature feature : landscape.features()) {
                final CiSpy witness = witnessProvider.spyFor(feature);
                if (witness.takeNoteOf(targetId, note)) {
                    return;
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to handle request to note a build", e);
        }
        finally {
            try {
                response.close();
            } catch (IOException e) {
                LOG.error("Failed to close response object", e);
            }
        }
    }

    private String getOriginator(Request request) {
        String forwardedFor = request.getValue("X-Forwarded-For");
        if (null != forwardedFor && !forwardedFor.isEmpty()) {
            return forwardedFor;
        }
        return request.getClientAddress().getHostName();
    }

}
