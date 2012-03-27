package org.netmelody.cieye.server.observation.test;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.netmelody.cieye.server.observation.GovernmentWatchdog;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GovernmentWatchdogTest {
    private final StubCommunicationNetwork network = new StubCommunicationNetwork();
    private final GovernmentWatchdog watchdog = new GovernmentWatchdog(network);
    
    @Test public void
    processesJsonResponse() {
        network.respondingWith("http://github.com/api/v2/json/repos/show/netmelody/ci-eye/tags", contentFrom("gh_2.0_tags.json"));
        network.respondingWith("https://api.github.com/repos/netmelody/ci-eye/tags", contentFrom("gh_3.0_tags.json"));
        assertThat(watchdog.getLatestVersion(), equalTo("0.3.0"));
    }

    @Test public void
    ranksTagsNumerically() {
        final String json_v2 = "{\"tags\":{\"0.0.2\":\"\",\"0.0.11\":\"\"}}";
        final String json_v3 = "[{\"name\": \"0.0.2\"}, {\"name\": \"0.0.11\"}]";
        
        network.respondingWith("http://github.com/api/v2/json/repos/show/netmelody/ci-eye/tags", json_v2);
        network.respondingWith("https://api.github.com/repos/netmelody/ci-eye/tags", json_v3);
        
        assertThat(watchdog.getLatestVersion(), equalTo("0.0.11"));
    }
    
    @Test public void
    ranksBetaTagsLower() {
        final String json_v2 = "{\"tags\":{\"0.0.1beta1\":\"\",\"0.0.1\":\"\"}}";
        final String json_v3 = "[{\"name\": \"0.0.1beta1\"}, {\"name\": \"0.0.1\"}]";
        
        network.respondingWith("http://github.com/api/v2/json/repos/show/netmelody/ci-eye/tags", json_v2);
        network.respondingWith("https://api.github.com/repos/netmelody/ci-eye/tags", json_v3);
        
        assertThat(watchdog.getLatestVersion(), equalTo("0.0.1"));
    }
    
    private String contentFrom(String resourceName) {
        try {
            return IOUtils.toString(GovernmentWatchdogTest.class.getResourceAsStream(resourceName));
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
