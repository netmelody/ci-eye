package org.netmelody.cieye.server.response;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netmelody.cieye.core.domain.Landscape;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.resource.Resource;

public final class DohHandler implements Resource {

    private static final Log LOG = LogFactory.getLog(DohHandler.class);
    
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
            prison.imprison(tracker.sponsorsOf(request, "-doh-"), landscape);
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
