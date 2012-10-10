package org.netmelody.cieye.server.response.responder;

import java.io.IOException;

import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.simpleframework.http.Request;

public final class RedirectResponder implements CiEyeResponder {

    private final String newLocation;

    public RedirectResponder(String newLocation) {
        this.newLocation = newLocation;
    }

    @Override
    public CiEyeResponse respond(Request request) throws IOException {
        return CiEyeResponse.movedPermanentlyTo(newLocation);
    }
}