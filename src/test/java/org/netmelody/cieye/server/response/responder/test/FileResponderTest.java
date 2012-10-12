package org.netmelody.cieye.server.response.responder.test;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.netmelody.cieye.server.response.responder.FileResponder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class FileResponderTest {

    @Test public void
    suppliesTheCorrectMimeType() throws IOException {
        final FileResponder responder = new FileResponder("/org/netmelody/cieye/server/response/responder/test/bob.js");
        assertThat(responder.respond(null).contentType, is("text/javascript; charset=utf-8"));
    }

    @Test public void
    writesTheCorrectContent() throws IOException {
        final FileResponder responder = new FileResponder("/org/netmelody/cieye/server/response/responder/test/bob.js");
        assertThat(IOUtils.toString(responder.respond(null).inputStream()), is("//hi"));
    }
}
