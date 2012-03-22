package org.netmelody.cieye.server.observation.protocol.test;

import org.junit.After;
import org.junit.Test;
import org.netmelody.cieye.server.observation.protocol.RestRequester;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

public final class RestRequesterTest {

    private final DummyServer server = new DummyServer();
    private final RestRequester requester = new RestRequester();

    @After
    public void shutdownServer() {
        requester.shutdown();
        server.close();
    }
    
    @Test public void
    makesASuccessfulRequest() {
        server.respondWith("some response text");
        assertThat(requester.makeRequest("http://localhost:" + server.port() + "/"), startsWith("some response text"));
    }
    
//    @Test public void
//    makesASuccessfulHttpsRequest() {
//        assertThat(requester.makeRequest("https://localhost:" + server.port() + "/"), startsWith("some response text"));
//    }
}