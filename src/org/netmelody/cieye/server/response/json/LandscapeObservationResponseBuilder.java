package org.netmelody.cieye.server.response.json;

import static java.lang.Math.min;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.server.LandscapeFetcher;
import org.netmelody.cieye.server.WitnessProvider;
import org.netmelody.cieye.server.response.JsonResponse;
import org.netmelody.cieye.server.response.JsonResponseBuilder;
import org.simpleframework.http.Path;

public final class LandscapeObservationResponseBuilder implements JsonResponseBuilder {

    private final LandscapeFetcher state;
    private final WitnessProvider witnessProvider;

    public LandscapeObservationResponseBuilder(LandscapeFetcher state, WitnessProvider witnessProvider) {
        this.state = state;
        this.witnessProvider = witnessProvider;
    }

    @Override
    public JsonResponse buildResponse(Path path, String requestContent) {
        TargetGroup response = new TargetGroup();
        long timeToLive = Long.MAX_VALUE;
        
        final String[] segments = path.getSegments();
        final Landscape landscape = state.landscapeNamed(segments[segments.length - 2]);
        
        for (Feature feature : landscape.features()) {
            final CiSpy witness = witnessProvider.witnessFor(feature);
            response = response.add(witness.statusOf(feature));
            timeToLive = min(timeToLive, witness.millisecondsUntilNextUpdate(feature));
        }
        
        return new JsonResponse(response, timeToLive);
    }
}
