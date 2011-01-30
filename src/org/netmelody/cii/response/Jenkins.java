package org.netmelody.cii.response;

import java.io.IOException;
import java.io.PrintStream;

import org.netmelody.cii.witness.jenkins.JenkinsWitness;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.resource.Resource;

public final class Jenkins implements Resource {

    @Override
    public void handle(Request request, Response response) {
        JenkinsWitness witness = new JenkinsWitness("http://deadlock.netbeans.org/hudson");
        
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
            response.setCode(500);
        }
    }
}
