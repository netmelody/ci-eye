package org.netmelody.cii.response;

import java.io.IOException;
import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.resource.Resource;

public final class JsonResponder implements Resource {

    private final JsonTranslator json = new JsonTranslator();
    private final JsonResponseBuilder responseBuilder;

    public JsonResponder(JsonResponseBuilder responseBuilder) {
        this.responseBuilder = responseBuilder;
    }

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
            
            JsonResponse jsonResponse = responseBuilder.buildResponse(request.getContent());
            body.println(json.toJson(jsonResponse.jsonContent()));
            body.close();        
        } catch (IOException e) {
            response.setCode(500);
        }
    }

}
