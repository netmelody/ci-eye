package org.netmelody.cii.response;

import java.io.IOException;
import java.io.PrintStream;

import org.netmelody.cii.witness.jenkins.JenkinsWitness;
import org.simpleframework.http.Address;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.resource.Resource;

import com.google.gson.Gson;

public final class JsonResponder implements Resource {

    private final Gson json = new Gson();
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
            
            JenkinsWitness witness = new JenkinsWitness("http://ccmain:8080");
            
            System.out.println(json.toJson(witness.targetList()));
            body.println(json.toJson(witness.targetList()));
            
            body.close();        
        } catch (IOException e) {
            response.setCode(500);
        }
    }

}
