package org.netmelody.cii.response.json;

import org.netmelody.cii.persistence.State;
import org.netmelody.cii.response.JsonResponse;
import org.netmelody.cii.response.JsonResponseBuilder;
import org.simpleframework.http.Query;

public final class LandscapeResponseBuilder implements JsonResponseBuilder {

    private final State state;

    public LandscapeResponseBuilder(State state) {
        this.state = state;
    }

    @Override
    public JsonResponse buildResponse(Query query, String requestContent) {
        return new JsonResponse(state.landscapes().landscapeNamed(query.get("name")));
    }

}
