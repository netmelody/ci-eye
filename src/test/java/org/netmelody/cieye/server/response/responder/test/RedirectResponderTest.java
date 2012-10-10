package org.netmelody.cieye.server.response.responder.test;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.netmelody.cieye.server.response.responder.RedirectResponder;
import org.simpleframework.http.Request;
import org.simpleframework.http.Status;

import com.google.common.collect.ImmutableMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class RedirectResponderTest {

    private final Mockery context = new Mockery();
    private final Request request = context.mock(Request.class);

    private final RedirectResponder redirect = new RedirectResponder("myNewLocation");

    @Test public void
    createsAValidHttpMovedPermanentlyResponse() throws Exception {
        final CiEyeResponse response = redirect.respond(request);
        assertThat(response.status, is(Status.MOVED_PERMANENTLY));
        assertThat(response.additionalStringHeaders, includesHeader("Location", "myNewLocation"));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Matcher<ImmutableMap<String, String>> includesHeader(String key, String value) {
        return (Matcher)Matchers.hasEntry(key, value);
    }
}
