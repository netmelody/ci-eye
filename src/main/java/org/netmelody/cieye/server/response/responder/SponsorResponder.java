package org.netmelody.cieye.server.response.responder;

import java.io.IOException;

import org.netmelody.cieye.core.domain.Sponsor;
import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.logging.Logbook;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.netmelody.cieye.server.response.JsonTranslator;
import org.netmelody.cieye.server.response.RequestOriginTracker;
import org.simpleframework.http.Request;

public final class SponsorResponder implements CiEyeResponder {

    private static final Logbook LOG = LogKeeper.logbookFor(SponsorResponder.class);
    
    private final RequestOriginTracker tracker;

    public SponsorResponder(RequestOriginTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public CiEyeResponse respond(Request request) throws IOException {
        String fingerprint = "";
        try {
            fingerprint = request.getForm().get("fingerprint");
            final Sponsor sponsor = tracker.sponsorWith(fingerprint);
            return CiEyeResponse.withJson(new JsonTranslator().toJson(sponsor)).expiringInMillis(10000L);
        } catch (Exception e) {
            LOG.error("Failed to handle request for sponsor with fingerprint " + fingerprint , e);
        }
        return CiEyeResponse.withJson("");
    }
}
