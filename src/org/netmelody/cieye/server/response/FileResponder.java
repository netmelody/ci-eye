package org.netmelody.cieye.server.response;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.resource.Resource;

public final class FileResponder implements Resource {

    private static final Log LOG = LogFactory.getLog(FileResponder.class);
    private static final ResourceBundle MIME_TYPES = ResourceBundle.getBundle(FileResponder.class.getName());
    
    private final String name;
    private final String extension;

    public FileResponder(String name) {
        this.name = name;
        this.extension = name.substring(name.lastIndexOf('.') + 1);
        LOG.info(name);
    }

    @Override
    public void handle(Request request, Response response) {
        InputStream input = null;
        OutputStream body = null;
        try {
            input = getClass().getResourceAsStream("/" + name);
            body = response.getOutputStream();
            long time = System.currentTimeMillis();
            response.set("Content-Type", contentTypeOf(extension));
            response.set("Server", "CiEye/1.0 (Simple 4.0)");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);
            IOUtils.copy(input, body);
        }
        catch (Exception e) {
            LOG.error("Failed to respond to request for resource " + this.name);
            response.setCode(Status.NOT_FOUND.getCode());
            response.setText(Status.NOT_FOUND.getDescription());
        }
        finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(body);
        }
    }
    
    private static String contentTypeOf(String extension) {
        return MIME_TYPES.getString(extension);
    }
}

