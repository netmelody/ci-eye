package org.netmelody.cieye.server.response;

import java.io.IOException;

import org.simpleframework.http.Request;

public interface CiEyeResponder {

    CiEyeResponse respond(Request request) throws IOException;
}
