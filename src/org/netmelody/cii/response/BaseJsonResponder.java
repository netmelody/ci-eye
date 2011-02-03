package org.netmelody.cii.response;

import java.io.IOException;
import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.resource.Resource;

import com.google.gson.Gson;

public abstract class BaseJsonResponder implements Resource {

    private final Gson json = new Gson();

    @Override
    public final void handle(Request request, Response response) {
        System.out.println(request.getAddress().getPath());
        
        PrintStream body;
        try {
            body = response.getPrintStream();
            long time = System.currentTimeMillis();
            response.set("Content-Type", "application/json");
            response.set("Server", "CiEye/1.0 (Simple 4.0)");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);
            response.setDate("Expires", time + 10000);
            
            body.println(json.toJson(jsonResponseObject()));
            body.close();        
        } catch (IOException e) {
            response.setCode(500);
        }
    }

    protected abstract Object jsonResponseObject();
    
}
