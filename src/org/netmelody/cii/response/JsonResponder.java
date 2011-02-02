package org.netmelody.cii.response;

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
    public void handle(Request req, Response resp) {
        System.out.println(target.getPath());
    }

}
