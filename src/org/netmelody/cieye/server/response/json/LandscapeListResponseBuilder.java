package org.netmelody.cieye.server.response.json;

import org.netmelody.cieye.server.configuration.State;
import org.netmelody.cieye.server.response.JsonResponse;
import org.netmelody.cieye.server.response.JsonResponseBuilder;
import org.simpleframework.http.Path;

public final class LandscapeListResponseBuilder implements JsonResponseBuilder {

    private final State state;

    public LandscapeListResponseBuilder(State state) {
        this.state = state;
    }

    @Override
    public JsonResponse buildResponse(Path path, String requestContent) {
        return new JsonResponse(state.landscapes());
    }

}
