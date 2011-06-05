package org.netmelody.cieye.server.response;

import java.io.IOException;

import org.netmelody.cieye.server.CiEyeServerInformationFetcher;
import org.simpleframework.http.Response;

public final class CiEyeVersionResponder implements CiEyeResponder {

    private final CiEyeServerInformationFetcher configurationFetcher;

    public CiEyeVersionResponder(CiEyeServerInformationFetcher configurationFetcher) {
        this.configurationFetcher = configurationFetcher;
    }

    @Override
    public void writeTo(Response response) throws IOException {
        response.set("Content-Type", "application/json");
        response.setDate("Expires", System.currentTimeMillis() + 10000L);
        response.getPrintStream().println(new JsonTranslator().toJson(configurationFetcher.getVersion()));
    }
    
}
