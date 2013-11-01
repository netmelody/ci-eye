package org.netmelody.cieye.server.response.responder;

import java.io.IOException;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.TargetId;
import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.logging.Logbook;
import org.netmelody.cieye.server.CiSpyIntermediary;
import org.netmelody.cieye.server.LandscapeFetcher;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.netmelody.cieye.server.response.RequestOriginTracker;
import org.simpleframework.http.Request;

public final class TargetNotationHandler implements CiEyeResponder {

    private static final Logbook LOG = LogKeeper.logbookFor(TargetNotationHandler.class);
    
    private final RequestOriginTracker tracker;
    private final LandscapeFetcher landscapeFetcher;
    private final CiSpyIntermediary spyIntermediary;

    public TargetNotationHandler(LandscapeFetcher landscapeFetcher, CiSpyIntermediary spyIntermediary, RequestOriginTracker tracker) {
        this.landscapeFetcher = landscapeFetcher;
        this.spyIntermediary = spyIntermediary;
        this.tracker = tracker;
    }

    private void makeNote(final String landscapeName, final TargetId targetId, final String note) {
        if (targetId.id() == null || targetId.id().isEmpty()) {
            return;
        }

        final Landscape landscape = landscapeFetcher.landscapeNamed(landscapeName);
        for (Feature feature : landscape.features()) {
            if (spyIntermediary.passNoteOn(feature, targetId, note)) {
                return;
            }
        }
    }

    @Override
    public CiEyeResponse respond(Request request) throws IOException {
        try {
            final String targetId = request.getForm().get("id");
            final String note = request.getForm().get("note") + " by " + tracker.originOf(request);
            final String[] segments = request.getAddress().getPath().getSegments();
            final String landscapeName = segments[segments.length - 2];
            makeNote(landscapeName, new TargetId(targetId), note);
        } catch (Exception e) {
            LOG.error("Failed to handle request to note a build", e);
        }
        return CiEyeResponse.withJson("");
    }
}
