package org.netmelody.cieye.server.response.resource;

import java.io.IOException;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.resource.Resource;

public final class NotFoundResource implements Resource {
    @Override
    public void handle(Request req, Response resp) {
        resp.setCode(Status.NOT_FOUND.getCode());
        resp.setText(Status.NOT_FOUND.getDescription());
        try {
            resp.getPrintStream().append("<!DOCTYPE html><html><head/><body>Page Not Found. Try <a href=\"/\">starting from the top<a></body></html>");
            resp.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}