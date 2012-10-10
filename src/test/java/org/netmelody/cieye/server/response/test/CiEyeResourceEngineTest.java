package org.netmelody.cieye.server.response.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.server.CiEyeNewVersionChecker;
import org.netmelody.cieye.server.CiEyeServerInformationFetcher;
import org.netmelody.cieye.server.CiSpyAllocator;
import org.netmelody.cieye.server.LandscapeFetcher;
import org.netmelody.cieye.server.PictureFetcher;
import org.netmelody.cieye.server.response.CiEyeResourceEngine;
import org.netmelody.cieye.server.response.RequestOriginTracker;
import org.netmelody.cieye.server.response.resource.CiEyeResource;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.parse.AddressParser;
import org.simpleframework.http.resource.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public final class CiEyeResourceEngineTest {

    private final Mockery context = new Mockery();
    
    private final LandscapeFetcher landscapeFetcher = context.mock(LandscapeFetcher.class);
    private final PictureFetcher pictureFetcher = context.mock(PictureFetcher.class);
    private final CiEyeServerInformationFetcher configurationFetcher = context.mock(CiEyeServerInformationFetcher.class);
    private final RequestOriginTracker tracker = context.mock(RequestOriginTracker.class);
    private final CiSpyAllocator allocator = context.mock(CiSpyAllocator.class);
    private final CiEyeNewVersionChecker updateChecker = context.mock(CiEyeNewVersionChecker.class);
    
    private final CiEyeResourceEngine engine = new CiEyeResourceEngine(landscapeFetcher, pictureFetcher, configurationFetcher,
                                                                       tracker, allocator, updateChecker);
    
    @Test public void
    resolvesWelcomePage() {
        final Resource resource = engine.resolve(new AddressParser("http://ci-eye/"));
        assertThat(resource, is(instanceOf(CiEyeResource.class)));
    }
    
    @Test public void
    indicatesResourcesThatAreNotFound() throws IOException {
        final Resource resource = engine.resolve(new AddressParser("http://ci-eye/sausage"));
        final Request request = context.mock(Request.class);
        final Response response = context.mock(Response.class);
        
        context.checking(new Expectations() {{
            oneOf(response).setCode(404);
            allowing(response).getOutputStream(); will(returnValue(new ByteArrayOutputStream()));
            ignoring(response);
        }});
        
        resource.handle(request, response);
        context.assertIsSatisfied();
    }
    
    @Test public void
    resolvesLandscapeList() {
        final Resource resource = engine.resolve(new AddressParser("http://ci-eye/landscapelist.json"));
        assertThat(resource, is(instanceOf(CiEyeResource.class)));
    }
    
    @Test public void
    resolvesSettingsLocation() {
        final Resource resource = engine.resolve(new AddressParser("http://ci-eye/settingslocation.json"));
        assertThat(resource, is(instanceOf(CiEyeResource.class)));
    }
    
    @Test public void
    resolvesVersion() {
        final Resource resource = engine.resolve(new AddressParser("http://ci-eye/version.json"));
        assertThat(resource, is(instanceOf(CiEyeResource.class)));
    }
    
    @Test public void
    resolvesTopLevelStaticFiles() {
        final Resource resource = engine.resolve(new AddressParser("http://ci-eye/favicon.ico"));
        assertThat(resource, is(instanceOf(CiEyeResource.class)));
    }

    @Test public void
    resolvesPictureResources() {
        final Resource resource = engine.resolve(new AddressParser("http://ci-eye/pictures/myMugshot.png"));
        assertThat(resource, is(instanceOf(CiEyeResource.class)));
    }

    @Test public void
    resolvesLandscapeResources() {
        final Resource resource = engine.resolve(new AddressParser("http://ci-eye/landscapes/myLandscape/"));
        assertThat(resource, is(instanceOf(CiEyeResource.class)));
    }

    @Test public void
    redirectsLandscapeResourcesWithoutTrailingSlash() throws IOException {
        final Resource resource = engine.resolve(new AddressParser("http://ci-eye/landscapes/myLandscape"));
        final Request request = context.mock(Request.class);
        final Response response = context.mock(Response.class);
        
        context.checking(new Expectations() {{
            oneOf(response).setCode(301);
            allowing(response).getOutputStream(); will(returnValue(new ByteArrayOutputStream()));
            ignoring(response);
        }});
        
        resource.handle(request, response);
        context.assertIsSatisfied();
    }

    @Test public void
    resolvesLandscapeObservationResources() {
        context.checking(new Expectations() {{
            oneOf(landscapeFetcher).landscapeNamed("myLandscape");
        }});
        
        final Resource resource = engine.resolve(new AddressParser("http://ci-eye/landscapes/myLandscape/landscapeobservation.json"));
        
        context.assertIsSatisfied();
        assertThat(resource, is(instanceOf(CiEyeResource.class)));
    }
    
    @Test public void
    handlesAddNoteRequest() {
        final Resource resource = engine.resolve(new AddressParser("http://ci-eye/landscapes/myLandscape/addNote"));
        assertThat(resource, is(instanceOf(CiEyeResource.class)));
    }
    
    @Test public void
    handlesDohRequest() {
        context.checking(new Expectations() {{
            oneOf(landscapeFetcher).landscapeNamed("myLandscape");
        }});
        
        final Resource resource = engine.resolve(new AddressParser("http://ci-eye/landscapes/myLandscape/doh"));
        
        context.assertIsSatisfied();
        assertThat(resource, is(instanceOf(CiEyeResource.class)));
    }
}