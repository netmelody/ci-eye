package org.netmelody.cii.response.json;

import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.Landscape;
import org.netmelody.cii.persistence.State;
import org.netmelody.cii.response.JsonResponse;
import org.netmelody.cii.response.JsonResponseBuilder;
import org.netmelody.cii.witness.Witness;
import org.netmelody.cii.witness.WitnessProvider;
import org.simpleframework.http.Query;

public final class LandscapeObservationResponseBuilder implements JsonResponseBuilder {

    private final State state;
    private final WitnessProvider witnessProvider;

    public LandscapeObservationResponseBuilder(State state, WitnessProvider witnessProvider) {
        this.state = state;
        this.witnessProvider = witnessProvider;
    }

    @Override
    public JsonResponse buildResponse(Query query, String requestContent) {
        final Landscape landscape = state.landscapeNamed(query.get("landscapeName"));
        final Feature feature = landscape.features().iterator().next();
        final Witness witness = witnessProvider.witnessFor(feature);
        return new JsonResponse(witness.statusOf(feature), witness.millisecondsUntilNextUpdate());
    }
}
