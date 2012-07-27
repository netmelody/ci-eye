package org.netmelody.cieye.server.observation.protocol.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

import org.junit.After;
import org.junit.Test;
import org.netmelody.cieye.server.observation.protocol.JsonRestRequester;

import com.google.gson.GsonBuilder;

public final class JsonRestRequesterTest {

    private final DummyServer server = new DummyServer();
    private final JsonRestRequester requester = new JsonRestRequester(new GsonBuilder().create());

    @After
    public void shutdownServer() {
        requester.shutdown();
        server.close();
    }
    
    @Test public void
    makesASuccessfulRequest() {
        server.respondWith("{ \"property\": \"value\" }");
        final DummyJson result = requester.makeJsonRestCall("http://localhost:" + server.port() + "/", DummyJson.class);
        assertThat(result, hasProperty("property", equalTo("value")));
    }
    
    public static final class DummyJson {
        private String property;
        
        public String getProperty() {
            return this.property;
        }
        
        @Override
        public String toString() {
            return "DummyJson with property:" + this.property;
        }
    }
}