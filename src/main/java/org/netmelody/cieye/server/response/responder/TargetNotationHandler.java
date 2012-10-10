package org.netmelody.cieye.server.response.responder;

import java.io.IOException;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.logging.Logbook;
import org.netmelody.cieye.server.CiSpyAllocator;
import org.netmelody.cieye.server.CiSpyHandler;
import org.netmelody.cieye.server.LandscapeFetcher;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.netmelody.cieye.server.response.RequestOriginTracker;
import org.simpleframework.http.Request;

public final class TargetNotationHandler implements CiEyeResponder {

    private static final Logbook LOG = LogKeeper.logbookFor(TargetNotationHandler.class);
    
    private final RequestOriginTracker tracker;
    private final LandscapeFetcher landscapeFetcher;
    private final CiSpyAllocator spyAllocator;

    public TargetNotationHandler(LandscapeFetcher landscapeFetcher, CiSpyAllocator spyAllocator, RequestOriginTracker tracker) {
        this.landscapeFetcher = landscapeFetcher;
        this.spyAllocator = spyAllocator;
        this.tracker = tracker;
    }

    private void makeNote(final String landscapeName, final String targetId, final String note) {
        if (targetId == null || targetId.isEmpty()) {
            return;
        }

        final Landscape landscape = landscapeFetcher.landscapeNamed(landscapeName);
        for (Feature feature : landscape.features()) {
            final CiSpyHandler spy = spyAllocator.spyFor(feature);
            if (spy.takeNoteOf(targetId, note)) {
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
            makeNote(landscapeName, targetId, note);
        } catch (Exception e) {
            LOG.error("Failed to handle request to note a build", e);
        }
        return CiEyeResponse.withJson("");
    }
}
