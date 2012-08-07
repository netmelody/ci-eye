package org.netmelody.cieye.server.configuration.avatar;

import org.junit.*;

import java.net.URL;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class SimpleHttpTest {
    private SimpleHttp subject = new SimpleHttp();

    @Test
    public void testImageUrlFor() throws Exception {
        assertThat(subject.handlesPrefix(), notNullValue());
    }

    @Test
    public void testHandlesPrefix() throws Exception {
        //http: part is stripped at PictureUrlRegistry level
        String avatarUrl = subject.imageUrlFor("//assets.github.com/images/modules/about_page/octocat.png");
        //Throws an exception if any errors are found
        URL url = new URL(avatarUrl);
        assertThat(url.toExternalForm(), notNullValue());
    }
}
