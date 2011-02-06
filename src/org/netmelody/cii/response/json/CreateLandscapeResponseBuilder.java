package org.netmelody.cii.response.json;

import org.netmelody.cii.domain.Landscape;
import org.netmelody.cii.persistence.State;
import org.netmelody.cii.response.JsonResponse;
import org.netmelody.cii.response.JsonResponseBuilder;

public final class CreateLandscapeResponseBuilder implements JsonResponseBuilder {

    private final State state;

    public CreateLandscapeResponseBuilder(State state) {
        this.state = state;
    }

    @Override
    public JsonResponse buildResponse(String requestContent) {
        final Landscape landscape = new Landscape(requestContent);
        state.addLandscape(landscape);
        return new JsonResponse(landscape);
    }

}
