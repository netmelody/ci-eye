package org.netmelody.cii.response.json;

import org.netmelody.cii.persistence.State;
import org.netmelody.cii.response.JsonResponse;
import org.netmelody.cii.response.JsonResponseBuilder;
import org.simpleframework.http.Path;

public final class LandscapeResponseBuilder implements JsonResponseBuilder {

    private final State state;

    public LandscapeResponseBuilder(State state) {
        this.state = state;
    }

    @Override
    public JsonResponse buildResponse(Path path, String requestContent) {
        final String[] segments = path.getSegments();
        return new JsonResponse(state.landscapeNamed(segments[segments.length - 1]));
    }

}
