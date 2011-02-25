package org.netmelody.cii.response;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.netmelody.cii.persistence.State;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.resource.Resource;

public final class PictureResponder implements Resource {

    private static final ResourceBundle MIME_TYPES = ResourceBundle.getBundle(FileResponder.class.getName());
    
    private final String name;
    private final String extension;
    private final State state;

    public PictureResponder(State state, Path path) {
        this.state = state;
        this.name = defaultString(path.getName(), "vlad.jpg");
        this.extension = defaultString(path.getExtension(), "jpg");
        System.out.println(path.getPath());
    }

    @Override
    public void handle(Request request, Response response) {
        FileInputStream picture = null;
        OutputStream body = null;
        try {
            picture = new FileInputStream(state.getPictureResource(name));
            body = response.getOutputStream();
            long time = System.currentTimeMillis();
            response.set("Content-Type", contentTypeOf(extension));
            response.set("Server", "CiEye/1.0 (Simple 4.0)");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);
            IOUtils.copy(picture, body);
        }
        catch (IOException e) {
            response.setCode(500);
        }
        finally {
            IOUtils.closeQuietly(picture);
            IOUtils.closeQuietly(body);
        }
    }

    private static String contentTypeOf(String extension) {
        return MIME_TYPES.getString(extension);
    }
    
    private static String defaultString(String value, String defaultValue) {
        return (null == value || value.isEmpty()) ? defaultValue : value;
    }
}

