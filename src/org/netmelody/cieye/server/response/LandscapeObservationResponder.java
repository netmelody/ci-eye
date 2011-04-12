package org.netmelody.cieye.server.response;

import static java.lang.Math.min;

import java.io.IOException;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.server.CiSpyAllocator;
import org.simpleframework.http.Response;

public final class LandscapeObservationResponder implements CiEyeResponder {

    private final CiSpyAllocator spyAllocator;
    private final Landscape landscape;

    public LandscapeObservationResponder(Landscape landscape, CiSpyAllocator spyAllocator) {
        this.landscape = landscape;
        this.spyAllocator = spyAllocator;
    }

    @Override
    public void writeTo(Response response) throws IOException {
        TargetGroup result = new TargetGroup();
        long timeToLive = Long.MAX_VALUE;
        for (Feature feature : landscape.features()) {
            final CiSpy witness = spyAllocator.spyFor(feature);
            result = result.add(witness.statusOf(feature));
            timeToLive = min(timeToLive, witness.millisecondsUntilNextUpdate(feature));
        }
        
        response.set("Content-Type", "application/json");
        response.setDate("Expires", System.currentTimeMillis() + timeToLive);
        response.getPrintStream().println(new JsonTranslator().toJson(result));
    }
}
