package org.netmelody.cii.response;

import java.io.IOException;
import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.resource.Resource;

public final class HelloWorld implements Resource {

    @Override
    public void handle(Request request, Response response) {
        PrintStream body;
        try {
            body = response.getPrintStream();
            long time = System.currentTimeMillis();
            response.set("Content-Type", "text/plain");
            response.set("Server", "HelloWorld/1.0 (Simple 4.0)");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);
            
            body.println("Hello World2");
            body.close();        
        } catch (IOException e) {
//            response.reset();
            response.setCode(500);
        }
    }
}
