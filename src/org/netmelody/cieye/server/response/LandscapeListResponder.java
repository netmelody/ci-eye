package org.netmelody.cieye.server.response;

import java.io.IOException;

import org.netmelody.cieye.server.LandscapeFetcher;
import org.simpleframework.http.Response;

public final class LandscapeListResponder implements CiEyeResponder {

    private final LandscapeFetcher landscapeFetcher;

    public LandscapeListResponder(LandscapeFetcher state) {
        this.landscapeFetcher = state;
    }

    @Override
    public void writeTo(Response response) throws IOException {
        response.set("Content-Type", "application/json");
        response.setDate("Expires", System.currentTimeMillis() + 10000L);
        response.getPrintStream().println(new JsonTranslator().toJson(landscapeFetcher.landscapes()));
    }
}
