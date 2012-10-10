package org.netmelody.cieye.server.response.responder;

import java.io.IOException;

import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.logging.Logbook;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.netmelody.cieye.server.response.Prison;
import org.netmelody.cieye.server.response.RequestOriginTracker;
import org.simpleframework.http.Request;

public final class DohHandler implements CiEyeResponder {

    private static final Logbook LOG = LogKeeper.logbookFor(DohHandler.class);
    
    private final Landscape landscape;
    private final RequestOriginTracker tracker;
    private final Prison prison;

    public DohHandler(Landscape landscape, Prison prison, RequestOriginTracker tracker) {
        this.landscape = landscape;
        this.prison = prison;
        this.tracker = tracker;
        
    }

    @Override
    public CiEyeResponse respond(Request request) throws IOException {
        try {
            final String active = request.getForm().get("active");
            if ("true".equals(active)) {
                prison.imprison(tracker.sponsorsOf(request, "-doh-"), landscape);
            }
            else {
                prison.paroleAllPrisonersFor(landscape);
            }
            
        } catch (Exception e) {
            LOG.error("Failed to handle request to doh", e);
        }
        return CiEyeResponse.withJson("");
    }
}
