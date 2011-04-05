package org.netmelody.cieye.server.response.json;

import org.netmelody.cieye.server.ConfigurationFetcher;
import org.netmelody.cieye.server.response.JsonResponse;
import org.netmelody.cieye.server.response.JsonResponseBuilder;
import org.simpleframework.http.Path;

public final class SettingsLocationResponseBuilder implements JsonResponseBuilder {

    private final ConfigurationFetcher state;

    public SettingsLocationResponseBuilder(ConfigurationFetcher state) {
        this.state = state;
    }

    @Override
    public JsonResponse buildResponse(Path path, String requestContent) {
        return new JsonResponse(state.settingsLocation());
    }
}
