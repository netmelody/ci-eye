package org.netmelody.cieye.server.response.responder;

import java.io.IOException;

import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.simpleframework.http.Request;
import org.simpleframework.http.Status;

public final class NotFoundResponder implements CiEyeResponder {

    private static final String CONTENT =
            "<!DOCTYPE html>" +
            "<html>" +
            "<body>Page Not Found. Try <a href=\"/\">starting from the top<a></body>" +
            "</html>";

    @Override
    public CiEyeResponse respond(Request request) throws IOException {
        return CiEyeResponse.withHtml(CONTENT).withStatus(Status.NOT_FOUND);
    }
}