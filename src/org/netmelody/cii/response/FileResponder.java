package org.netmelody.cii.response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.resource.Resource;

public final class FileResponder implements Resource {

    private final String name;
    private final String extension;

    public FileResponder(Path path) {
        this.name = defaultString(path.getName(), "cieye.html");
        this.extension = defaultString(path.getExtension(), "html");
        System.out.println(path.getPath());
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
        catch (IOException e) {
            response.setCode(500);
        }
        finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(body);
        }
    }

    private static String contentTypeOf(String extension) {
        return ResourceBundle.getBundle(FileResponder.class.getName()).getString(extension);
    }
    
    private static String defaultString(String value, String defaultValue) {
        return (null == value || value.isEmpty()) ? defaultValue : value;
    }
}

