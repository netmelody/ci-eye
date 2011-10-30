package org.netmelody.cieye.server.response;

import java.io.IOException;

import org.simpleframework.http.Response;

public interface CiEyeResponder {

    void writeTo(Response response) throws IOException;
}
