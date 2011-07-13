package org.netmelody.cieye.server.response.responder.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
    
    @Test public void
    writesTheCorrectContent() throws IOException {
        final FileResponder responder = new FileResponder("org/netmelody/cieye/server/response/responder/test/bob.js");
        final OutputStream outputStream = new ByteArrayOutputStream();
        final Response response = context.mock(Response.class);
        context.checking(new Expectations() {{
            allowing(response).getOutputStream(); will(returnValue(outputStream));
            
            ignoring(response).set(with(any(String.class)), with(any(String.class)));
        }});
        
        responder.writeTo(response);
        
        assertThat(outputStream.toString(), is("//hi"));
    }
}
