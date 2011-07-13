package org.netmelody.cieye.server.response.responder.test;

import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.server.response.responder.FileResponder;
import org.simpleframework.http.Response;

import com.google.common.io.NullOutputStream;

public class FileResponderTest {

    private final Mockery context = new Mockery();
    
    @Test public void
    suppliesTheCorrectMimeType() throws IOException {
        final FileResponder responder = new FileResponder("org/netmelody/cieye/server/response/responder/test/bob.js");
        
        final Response response = context.mock(Response.class);
        context.checking(new Expectations() {{
            oneOf(response).set("Content-Type", "text/javascript");
            
            allowing(response).getOutputStream(); will(returnValue(new NullOutputStream()));
        }});
        
        responder.writeTo(response);
        context.assertIsSatisfied();
    }

}
