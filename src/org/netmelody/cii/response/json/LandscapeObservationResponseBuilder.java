package org.netmelody.cii.response.json;

import static java.lang.Math.min;

import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.Landscape;
import org.netmelody.cii.domain.TargetGroup;
import org.netmelody.cii.persistence.State;
import org.netmelody.cii.response.JsonResponse;
import org.netmelody.cii.response.JsonResponseBuilder;
import org.netmelody.cii.witness.Witness;
import org.netmelody.cii.witness.WitnessProvider;
import org.simpleframework.http.Path;

public final class LandscapeObservationResponseBuilder implements JsonResponseBuilder {

    private final State state;
    private final WitnessProvider witnessProvider;

    public LandscapeObservationResponseBuilder(State state, WitnessProvider witnessProvider) {
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
            final Witness witness = witnessProvider.witnessFor(feature);
            response = response.add(witness.statusOf(feature));
            timeToLive = min(timeToLive, witness.millisecondsUntilNextUpdate());
        }
        
        return new JsonResponse(response, timeToLive);
    }
}
