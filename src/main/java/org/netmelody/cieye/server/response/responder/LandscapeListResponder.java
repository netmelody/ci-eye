package org.netmelody.cieye.server.response.responder;

import java.io.IOException;

import org.netmelody.cieye.server.LandscapeFetcher;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.netmelody.cieye.server.response.JsonTranslator;
import org.simpleframework.http.Request;

public final class LandscapeListResponder implements CiEyeResponder {

    private final LandscapeFetcher landscapeFetcher;

    public LandscapeListResponder(LandscapeFetcher landscapeFetcher) {
        this.landscapeFetcher = landscapeFetcher;
    }

    @Override
    public CiEyeResponse respond(Request request) throws IOException {
        return CiEyeResponse.withJson(new JsonTranslator().toJson(landscapeFetcher.landscapes())).expiringInMillis(10000L);
    }
}
