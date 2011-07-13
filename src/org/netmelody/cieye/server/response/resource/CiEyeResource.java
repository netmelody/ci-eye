package org.netmelody.cieye.server.response.resource;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.resource.Resource;

public final class CiEyeResource implements Resource {

    private static final Log LOG = LogFactory.getLog(CiEyeResource.class);
    
    private final CiEyeResponder responder;

    public CiEyeResource(CiEyeResponder responder) {
        this.responder = responder;
    }
    
    @Override
    public void handle(Request request, Response response) {
        try {
            long time = System.currentTimeMillis();
            response.set("Content-Type", "text/html");
            response.set("Server", "CiEye/1.0 (Simple 4.0)");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);
            responder.writeTo(response);
        }
        catch (Exception e) {
            LOG.error("Failed to respond to request for resource " + request.getPath().getPath(), e);
            response.setCode(Status.NOT_FOUND.getCode());
            response.setText(Status.NOT_FOUND.getDescription());
        }
        finally {
            try {
                response.close();
            } catch (IOException e) {
                LOG.error("Failed to close response for resource " + request.getPath().getPath(), e);
            }
        }
    }
}
