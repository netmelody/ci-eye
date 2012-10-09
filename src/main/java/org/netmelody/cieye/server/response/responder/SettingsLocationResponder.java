package org.netmelody.cieye.server.response.responder;

import java.io.IOException;

import org.netmelody.cieye.server.CiEyeServerInformationFetcher;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.netmelody.cieye.server.response.JsonTranslator;
import org.simpleframework.http.Request;

public final class SettingsLocationResponder implements CiEyeResponder {

    private final CiEyeServerInformationFetcher configurationFetcher;

    public SettingsLocationResponder(CiEyeServerInformationFetcher configurationFetcher) {
        this.configurationFetcher = configurationFetcher;
    }

    @Override
    public CiEyeResponse respond(Request request) throws IOException {
        return CiEyeResponse.withJson(new JsonTranslator().toJson(configurationFetcher.settingsLocation())).expiringInMillis(10000L);
    }
}
