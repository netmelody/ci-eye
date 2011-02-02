package org.netmelody.cii.response;

import java.io.IOException;
import java.io.PrintStream;

import org.simpleframework.http.Address;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.resource.Resource;

public final class JsonResponder implements Resource {

    private final Address target;

    public JsonResponder(Address target) {
        this.target = target;
    }

    @Override
    public void handle(Request request, Response response) {
        System.out.println(target.getPath());
        
        PrintStream body;
        try {
            body = response.getPrintStream();
            long time = System.currentTimeMillis();
            response.set("Content-Type", "application/json");
            response.set("Server", "CiEye/1.0 (Simple 4.0)");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);
            response.setDate("Expires", time + 10000);
            
            body.println("{\"jobs\":[{\"name\":\"HIP\"},{\"name\":\"IDS-HIP\"}]}");
            body.close();        
        } catch (IOException e) {
            response.setCode(500);
        }
    }

}
