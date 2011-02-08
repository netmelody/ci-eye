package org.netmelody.cii.response.json;

import org.netmelody.cii.domain.CiServerType;
import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.Landscape;
import org.netmelody.cii.persistence.State;
import org.netmelody.cii.response.JsonResponse;
import org.netmelody.cii.response.JsonResponseBuilder;
import org.netmelody.cii.witness.DummyWitness;
import org.netmelody.cii.witness.Witness;
import org.netmelody.cii.witness.jenkins.JenkinsWitness;
import org.simpleframework.http.Query;

public final class LandscapeObservationResponseBuilder implements JsonResponseBuilder {

    private final State state;

    public LandscapeObservationResponseBuilder(State state) {
        this.state = state;
    }

    @Override
    public JsonResponse buildResponse(Query query, String requestContent) {
        final Landscape landscape = state.landscapeNamed(query.get("landscapeName"));
        final Feature feature = landscape.features().iterator().next();
        
        Witness witness = new DummyWitness();
        if (CiServerType.JENKINS.equals(feature.type())) {
            witness = new JenkinsWitness(feature.endpoint());
        }
        
        return new JsonResponse(witness.statusOf(feature));
    }

}
