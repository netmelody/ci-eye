package org.netmelody.cii.response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.resource.Index;
import org.simpleframework.http.resource.Resource;

public final class FileResponder implements Resource {

    private final Index index;

    public FileResponder(Index index) {
        this.index = index;
        System.out.println(index.getRealPath());
    }

    @Override
    public void handle(Request request, Response response) {
        InputStream input = null;
        OutputStream body = null;
        try {
            input = getClass().getResourceAsStream("/" + index.getName());
            body = response.getOutputStream();
            long time = System.currentTimeMillis();
            response.set("Content-Type", index.getContentType());
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
}
