package org.netmelody.cieye.server.response.responder;

import static java.lang.Math.min;

import java.io.IOException;
import java.util.Set;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.Sponsor;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.server.CiSpyAllocator;
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
        TargetGroup result = new TargetGroup();
        long timeToLive = Long.MAX_VALUE;
        for (Feature feature : landscape.features()) {
            final CiSpy witness = spyAllocator.spyFor(feature);
            result = result.add(witness.statusOf(feature));
            timeToLive = min(timeToLive, witness.millisecondsUntilNextUpdate(feature));
        }
        
        if (prison.crimeReported(landscape)) {
            Set<Sponsor> dohGroup = prison.prisonersFor(landscape);
            result = result.withDoh(dohGroup);
        }
        
        response.set("Content-Type", "application/json");
        response.setDate("Expires", System.currentTimeMillis() + timeToLive);
        response.getPrintStream().println(new JsonTranslator().toJson(result));
    }
}
