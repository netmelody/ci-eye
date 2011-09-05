package org.netmelody.cieye.server.response.resource;

import java.io.IOException;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.resource.Resource;

public final class RedirectResource implements Resource {
    
    private final String newLocation;

    public RedirectResource(String newLocation) {
        this.newLocation = newLocation;
    }
    
    @Override
    public void handle(Request req, Response resp) {
        resp.setCode(Status.MOVED_PERMANENTLY.getCode());
        resp.setText(Status.MOVED_PERMANENTLY.getDescription());
        resp.set("Location", newLocation);
        try {
            resp.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}