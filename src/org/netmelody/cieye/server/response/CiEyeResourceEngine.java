package org.netmelody.cieye.server.response;

import java.io.IOException;

import org.netmelody.cieye.server.CiEyeServerInformationFetcher;
import org.netmelody.cieye.server.CiSpyAllocator;
import org.netmelody.cieye.server.LandscapeFetcher;
import org.netmelody.cieye.server.PictureFetcher;
import org.netmelody.cieye.server.response.responder.CiEyeVersionResponder;
import org.netmelody.cieye.server.response.responder.FileResponder;
import org.netmelody.cieye.server.response.responder.LandscapeListResponder;
import org.netmelody.cieye.server.response.responder.LandscapeObservationResponder;
import org.netmelody.cieye.server.response.responder.PictureResponder;
import org.netmelody.cieye.server.response.responder.SettingsLocationResponder;
import org.simpleframework.http.Address;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.resource.Resource;
import org.simpleframework.http.resource.ResourceEngine;

public final class CiEyeResourceEngine implements ResourceEngine {
    
    private final CiSpyAllocator allocator;
    private final LandscapeFetcher landscapeFetcher;
    private final PictureFetcher pictureFetcher;
    private final CiEyeServerInformationFetcher configurationFetcher;
    private final RequestOriginTracker tracker;
    private final Prison prison = new Prison();

    public CiEyeResourceEngine(LandscapeFetcher landscapeFetcher, PictureFetcher pictureFetcher,
                               CiEyeServerInformationFetcher configurationFetcher,
                               RequestOriginTracker tracker, CiSpyAllocator allocator) {
        
        this.landscapeFetcher = landscapeFetcher;
        this.pictureFetcher = pictureFetcher;
        this.configurationFetcher = configurationFetcher;
        this.tracker = tracker;
        this.allocator = allocator;
    }
    
    @Override
    public Resource resolve(Address target) {
        final String[] path = target.getPath().getSegments();
        
        if (path.length == 0) {
            return new CiEyeResource(new FileResponder("welcome.html"));
        }
        
        if (path.length == 1) {
            if ("landscapelist.json".equals(path[0])) {
                return new CiEyeResource(new LandscapeListResponder(landscapeFetcher));
            }
            if ("settingslocation.json".equals(path[0])) {
                return new CiEyeResource(new SettingsLocationResponder(configurationFetcher));
            }
            if ("version.json".equals(path[0])) {
                return new CiEyeResource(new CiEyeVersionResponder(configurationFetcher));
            }
            return new CiEyeResource(new FileResponder(path[0]));
        }
        
        if (path.length == 2) {
            if ("pictures".equals(path[0])) {
                return new CiEyeResource(new PictureResponder(pictureFetcher, path[1]));
            }
            
            if ("landscapes".equals(path[0])) {
                return new CiEyeResource(new FileResponder("cieye.html"));
            }
        }
        
        if (path.length == 3) {
            if ("landscapes".equals(path[0]) && "landscapeobservation.json".equals(path[2])) {
                return new CiEyeResource(new LandscapeObservationResponder(landscapeFetcher.landscapeNamed(path[1]), allocator, prison));
            }
            
            if ("landscapes".equals(path[0]) && "addNote".equals(path[2])) {
                return new TargetNotationHandler(landscapeFetcher, allocator, tracker);
            }
            
            if ("landscapes".equals(path[0]) && "doh".equals(path[2])) {
                return new DohHandler(landscapeFetcher.landscapeNamed(path[1]), prison, tracker);
            }
        }
        
        return new NotFoundResource();
    }
    
    public static final class NotFoundResource implements Resource {
        @Override
        public void handle(Request req, Response resp) {
            resp.setCode(Status.NOT_FOUND.getCode());
            resp.setText(Status.NOT_FOUND.getDescription());
            try {
                resp.getPrintStream().append("<!DOCTYPE html><html><head/><body>Page Not Found. Try <a href=\"/\">starting from the top<a></body></html>");
                resp.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}