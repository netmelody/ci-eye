package org.netmelody.cieye.server.response.responder;

import static java.lang.Math.min;

import java.io.IOException;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.LandscapeObservation;
import org.netmelody.cieye.server.CiSpyAllocator;
import org.netmelody.cieye.server.CiSpyHandler;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.JsonTranslator;
import org.netmelody.cieye.server.response.Prison;
import org.simpleframework.http.Response;

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
    public void writeTo(Response response) throws IOException {
        LandscapeObservation result = new LandscapeObservation();
        long timeToLive = Long.MAX_VALUE;
        for (Feature feature : landscape.features()) {
            final CiSpyHandler spy = spyAllocator.spyFor(feature);
            result = result.add(spy.statusOf(feature));
            timeToLive = min(timeToLive, spy.millisecondsUntilNextUpdate(feature));
        }
        
        if (prison.crimeReported(landscape)) {
            result = result.withDoh(prison.prisonersFor(landscape));
        }
        
        response.set("Content-Type", "application/json; charset=utf-8");
        response.setDate("Expires", System.currentTimeMillis() + timeToLive);
        response.getPrintStream().println(new JsonTranslator().toJson(result));
    }
}
