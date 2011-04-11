package org.netmelody.cieye.server.response;

import java.io.IOException;

import org.netmelody.cieye.server.CiSpyAllocator;
import org.netmelody.cieye.server.ConfigurationFetcher;
import org.netmelody.cieye.server.LandscapeFetcher;
import org.netmelody.cieye.server.PictureFetcher;
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
    private final ConfigurationFetcher configurationFetcher;
    private final CachedRequestOriginTracker tracker = new CachedRequestOriginTracker();

    public CiEyeResourceEngine(LandscapeFetcher landscapeFetcher, PictureFetcher pictureFetcher,
                               ConfigurationFetcher configurationFetcher, CiSpyAllocator allocator) {
        
        this.landscapeFetcher = landscapeFetcher;
        this.pictureFetcher = pictureFetcher;
        this.configurationFetcher = configurationFetcher;
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
                return new CiEyeResource(new LandscapeListResponseBuilder(landscapeFetcher));
            }
            if ("settingslocation.json".equals(path[0])) {
                return new CiEyeResource(new SettingsLocationResponseBuilder(configurationFetcher));
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
                return new CiEyeResource(new LandscapeObservationResponseBuilder(landscapeFetcher.landscapeNamed(path[1]), allocator));
            }
            
            if ("landscapes".equals(path[0]) && "addNote".equals(path[2])) {
                return new TargetNotationHandler(landscapeFetcher, allocator, tracker);
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