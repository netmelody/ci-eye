package org.netmelody.cieye.server.observation.protocol.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import org.junit.After;
import org.junit.Test;
import org.netmelody.cieye.server.observation.protocol.JsonRestRequester;

import com.google.gson.GsonBuilder;

public final class JsonRestRequesterTest {

    private final DummyServer server = new DummyServer();
    private final JsonRestRequester requester = new JsonRestRequester(new GsonBuilder().create(), server.port());

    @After
    public void shutdownServer() {
        requester.shutdown();
        server.close();
    }
    
    @Test public void
    makesASuccessfulRequest() {
        server.respondWith("{ \"property\": \"value\" }");
        assertThat(requester.makeJsonRestCall("http://localhost/", DummyJson.class),
                   both(is(instanceOf(DummyJson.class)))
                   .and(hasProperty("property", equalTo("value"))));
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
