package org.netmelody.cieye.server.observation.protocol.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.After;
import org.junit.Test;
import org.netmelody.cieye.server.observation.protocol.RestRequester;

public final class RestRequesterTest {

    private final DummyServer server = new DummyServer();
    private final RestRequester requester = new RestRequester(server.port());

    @After
    public void shutdownServer() {
        requester.shutdown();
        server.close();
    }
    
    @Test public void
    makesASuccessfulRequest() {
        server.respondWith("some response text");
        assertThat(requester.makeRequest("http://localhost/"), is("some response text"));
    }
}
