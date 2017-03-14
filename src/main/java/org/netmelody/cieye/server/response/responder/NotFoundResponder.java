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
            "<body>%s</body>" +
            "</html>";

    private static final String BODY = "Page Not Found. Try <a href=\"/\">starting from the top<a>";
    private final String content;

    public NotFoundResponder() {
        this(BODY);
    }

    public NotFoundResponder(String body) {
        this.content = String.format(CONTENT, body);
    }

    @Override
    public CiEyeResponse respond(Request request) throws IOException {
        return CiEyeResponse.withHtml(content).withStatus(Status.NOT_FOUND);
    }
}