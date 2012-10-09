package org.netmelody.cieye.server.response.responder;

import java.io.IOException;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.LandscapeObservation;
import org.netmelody.cieye.server.CiSpyAllocator;
import org.netmelody.cieye.server.CiSpyHandler;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.netmelody.cieye.server.response.JsonTranslator;
import org.netmelody.cieye.server.response.Prison;
import org.simpleframework.http.Request;

import static java.lang.Math.min;

public final class LandscapeObservationResponder implements CiEyeResponder {

    private final CiSpyAllocator spyAllocator;
    private final Landscape landscape;
    private final Prison prison;

    public LandscapeObservationResponder(Landscape landscape, CiSpyAllocator spyAllocator, Prison prison) {
        this.landscape = landscape;
        this.spyAllocator = spyAllocator;
        this.prison = prison;
    }

    @Override
    public CiEyeResponse respond(Request request) throws IOException {
        LandscapeObservation result = new LandscapeObservation();
        long timeToLiveMillis = Long.MAX_VALUE;
        for (Feature feature : landscape.features()) {
            final CiSpyHandler spy = spyAllocator.spyFor(feature);
            result = result.add(spy.statusOf(feature));
            timeToLiveMillis = min(timeToLiveMillis, spy.millisecondsUntilNextUpdate(feature));
        }
        
        if (prison.crimeReported(landscape)) {
            result = result.withDoh(prison.prisonersFor(landscape));
        }
        
        return CiEyeResponse.withJson(new JsonTranslator().toJson(result)).expiringInMillis(timeToLiveMillis);
    }
}
