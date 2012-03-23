package org.netmelody.cieye.server.response.resource;

import java.io.IOException;

import org.netmelody.cieye.core.domain.Sponsor;
import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.logging.Logbook;
import org.netmelody.cieye.server.response.JsonTranslator;
import org.netmelody.cieye.server.response.RequestOriginTracker;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.resource.Resource;

public final class SponsorResource implements Resource {

    private static final Logbook LOG = LogKeeper.logbookFor(SponsorResource.class);
    
    private final RequestOriginTracker tracker;

    public SponsorResource(RequestOriginTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void handle(Request request, Response response) {
        String fingerprint = "";
        try {
            fingerprint = request.getForm().get("fingerprint");
            final Sponsor sponsor = tracker.sponsorWith(fingerprint);
            long time = System.currentTimeMillis();
            response.set("Content-Type", "application/json");
            response.set("Server", "CiEye/1.0 (Simple 4.0)");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);
            response.setDate("Expires", System.currentTimeMillis() + 10000L);
            response.getPrintStream().println(new JsonTranslator().toJson(sponsor));
        } catch (Exception e) {
            LOG.error("Failed to handle request for sponsor with fingerprint " + fingerprint , e);
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
