package org.netmelody.cieye.server.response.responder;

import java.io.IOException;

import org.netmelody.cieye.server.LandscapeFetcher;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.JsonTranslator;
import org.simpleframework.http.Response;

public final class LandscapeListResponder implements CiEyeResponder {

    private final LandscapeFetcher landscapeFetcher;

    public LandscapeListResponder(LandscapeFetcher landscapeFetcher) {
        this.landscapeFetcher = landscapeFetcher;
    }

    @Override
    public void writeTo(Response response) throws IOException {
        response.set("Content-Type", "application/json");
        response.setDate("Expires", System.currentTimeMillis() + 10000L);
        response.getPrintStream().println(new JsonTranslator().toJson(landscapeFetcher.landscapes()));
    }
}
