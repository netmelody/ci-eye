package org.netmelody.cieye.server.response.resource;

import java.io.IOException;

import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.logging.Logbook;
import org.netmelody.cieye.server.response.Prison;
import org.netmelody.cieye.server.response.RequestOriginTracker;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.resource.Resource;

public final class DohHandler implements Resource {

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
    public void handle(Request request, Response response) {
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
        finally {
            try {
                response.close();
            } catch (IOException e) {
                LOG.error("Failed to close response object", e);
            }
        }
    }

}
