package org.netmelody.cii.response.json;

import org.netmelody.cii.persistence.State;
import org.netmelody.cii.response.JsonResponse;
import org.netmelody.cii.response.JsonResponseBuilder;

public final class LandscapeListResponseBuilder implements JsonResponseBuilder {

    private final State state;

    public LandscapeListResponseBuilder(State state) {
        this.state = state;
    }

    @Override
    public JsonResponse buildResponse(String requestContent) {
        return new JsonResponse(state.landscapes());
    }

}
