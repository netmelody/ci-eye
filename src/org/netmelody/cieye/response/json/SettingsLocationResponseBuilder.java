package org.netmelody.cieye.response.json;

import org.netmelody.cieye.persistence.State;
import org.netmelody.cieye.response.JsonResponse;
import org.netmelody.cieye.response.JsonResponseBuilder;
import org.simpleframework.http.Path;

public final class SettingsLocationResponseBuilder implements JsonResponseBuilder {

    private final State state;

    public SettingsLocationResponseBuilder(State state) {
        this.state = state;
    }

    @Override
    public JsonResponse buildResponse(Path path, String requestContent) {
        return new JsonResponse(state.settingsLocation());
    }
}
