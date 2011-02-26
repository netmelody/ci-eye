package org.netmelody.cii.response.json;

import org.netmelody.cii.persistence.State;
import org.netmelody.cii.response.JsonResponse;
import org.netmelody.cii.response.JsonResponseBuilder;
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
