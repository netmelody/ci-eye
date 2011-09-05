package org.netmelody.cieye.server.response.resource.test;

import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.server.response.resource.RedirectResource;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public final class RedirectResourceTest {

    private final Mockery context = new Mockery();
    
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    
    private final RedirectResource redirect = new RedirectResource("myNewLocation");
    
    @Test public void
    createsAValidHttpMovedPermanentlyResponse() throws Exception {
        context.checking(new Expectations() {{
            oneOf(response).setCode(301);
            oneOf(response).setText("Moved Permanently");
            oneOf(response).set("Location", "myNewLocation");
            
            ignoring(response).close();
        }});
        
        redirect.handle(request, response);
        context.assertIsSatisfied();
    }
    
    @Test public void
    closesTheResource() throws Exception {
        context.checking(new Expectations() {{
            oneOf(response).close();
            
            ignoring(response);
        }});
        
        redirect.handle(request, response);
        context.assertIsSatisfied();
    }
}
