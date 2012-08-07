package org.netmelody.cieye.server.configuration.avatar;

import org.junit.*;

import java.net.URL;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class RobohashTest {

    private Robohash subject = new Robohash();

    @Test
    public void testImageUrlFor() throws Exception {
        assertThat(subject.handlesPrefix(), notNullValue());
    }

    @Test
    public void testHandlesPrefix() throws Exception {
        String avatarUrl = subject.imageUrlFor("test@me.com");
        //Throws an exception if any errors are found
        URL url = new URL(avatarUrl);
        assertThat(url.toExternalForm(), notNullValue());
    }
}
