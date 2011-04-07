package org.netmelody.cieye.server.response;

import org.netmelody.cieye.server.CiSpyAllocator;
import org.netmelody.cieye.server.ConfigurationFetcher;
import org.netmelody.cieye.server.LandscapeFetcher;
import org.netmelody.cieye.server.PictureFetcher;
import org.simpleframework.http.Address;
import org.simpleframework.http.Path;
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
        if ("json".equals(target.getPath().getExtension())) {
            return new JsonResponder(jsonResponseBuilderFor(target));
        }
        if ("addNote".equals(target.getPath().getName())) {
            return new TargetNotationHandler(landscapeFetcher, allocator, tracker);
        }
        
        if ((target.getPath().getSegments().length > 0) && "/pictures".equals(target.getPath().getPath(0, 1))) {
            return new PictureResponder(pictureFetcher, target.getPath());
        }
        return new FileResponder(target.getPath());
    }

    private JsonResponseBuilder jsonResponseBuilderFor(Address target) {
        final String name = target.getPath().getName();
        
        if ("landscapeobservation.json".equals(name)) {
            return new LandscapeObservationResponseBuilder(landscapeFetcher, allocator);
        }
        
        if ("landscapelist.json".equals(name)) {
            return new LandscapeListResponseBuilder(landscapeFetcher);
        }
        
        if ("settingslocation.json".equals(name)) {
            return new SettingsLocationResponseBuilder(configurationFetcher);
        }
        
        return new JsonResponseBuilder() {
            @Override
            public JsonResponse buildResponse(Path path, String requestContent) {
                return new JsonResponse("");
            }
        };
    }
}