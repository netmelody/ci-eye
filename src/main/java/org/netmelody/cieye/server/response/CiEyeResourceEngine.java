package org.netmelody.cieye.server.response;


import org.netmelody.cieye.server.CiEyeNewVersionChecker;
import org.netmelody.cieye.server.CiEyeServerInformationFetcher;
import org.netmelody.cieye.server.CiSpyIntermediary;
import org.netmelody.cieye.server.LandscapeFetcher;
import org.netmelody.cieye.server.PictureFetcher;
import org.netmelody.cieye.server.response.resource.CiEyeResource;
import org.netmelody.cieye.server.response.responder.CiEyeVersionResponder;
import org.netmelody.cieye.server.response.responder.DohHandler;
import org.netmelody.cieye.server.response.responder.FileResponder;
import org.netmelody.cieye.server.response.responder.LandscapeListResponder;
import org.netmelody.cieye.server.response.responder.LandscapeObservationResponder;
import org.netmelody.cieye.server.response.responder.NotFoundResponder;
import org.netmelody.cieye.server.response.responder.PictureResponder;
import org.netmelody.cieye.server.response.responder.RedirectResponder;
import org.netmelody.cieye.server.response.responder.SettingsLocationResponder;
import org.netmelody.cieye.server.response.responder.SponsorResponder;
import org.netmelody.cieye.server.response.responder.TargetNotationHandler;
import org.simpleframework.http.Address;
import org.simpleframework.http.resource.Resource;
import org.simpleframework.http.resource.ResourceEngine;

public final class CiEyeResourceEngine implements ResourceEngine {
    
    private final CiSpyIntermediary spyIntermediary;
    private final LandscapeFetcher landscapeFetcher;
    private final PictureFetcher pictureFetcher;
    private final CiEyeServerInformationFetcher configurationFetcher;
    private final CiEyeNewVersionChecker updateChecker;
    private final RequestOriginTracker tracker;
    private final Prison prison = new Prison();

    public CiEyeResourceEngine(LandscapeFetcher landscapeFetcher, PictureFetcher pictureFetcher,
                               CiEyeServerInformationFetcher configurationFetcher,
                               RequestOriginTracker tracker, CiSpyIntermediary spyIntermediary,
                               CiEyeNewVersionChecker updateChecker) {
        
        this.landscapeFetcher = landscapeFetcher;
        this.pictureFetcher = pictureFetcher;
        this.configurationFetcher = configurationFetcher;
        this.tracker = tracker;
        this.spyIntermediary = spyIntermediary;
        this.updateChecker = updateChecker;
    }

    @Override
    public Resource resolve(Address target) {
        return new CiEyeResource(route(target));
    }

    private CiEyeResponder route(Address target) {
        final String[] path = target.getPath().getSegments();
        
        if (path.length == 0) {
            return new FileResponder("/resources/welcome.html");
        }
        
        if (path.length == 1) {
            if ("mugshotconfig.html".equals(path[0])) {
                return new FileResponder("/resources/mugshotconfig.html");
            }
            if ("landscapelist.json".equals(path[0])) {
                return new LandscapeListResponder(landscapeFetcher);
            }
            if ("settingslocation.json".equals(path[0])) {
                return new SettingsLocationResponder(configurationFetcher);
            }
            if ("version.json".equals(path[0])) {
                return new CiEyeVersionResponder(configurationFetcher, updateChecker);
            }
            if ("sponsor.json".equals(path[0])) {
                return new SponsorResponder(tracker);
            }
            
            final String name = "/resources/" + path[0];
            if (null != getClass().getResource(name)) {
                return new FileResponder(name);
            }
        }
        
        if (path.length == 2) {
            if ("pictures".equals(path[0])) {
                return new PictureResponder(pictureFetcher, path[1]);
            }
            
            if ("landscapes".equals(path[0])) {
                if (!target.getPath().getPath().endsWith("/")) {
                    return new RedirectResponder(target.getPath().getPath() + "/");
                }
                return new FileResponder("/resources/cieye.html");
            }
        }
        
        if (path.length == 3) {
            if ("landscapes".equals(path[0]) && "landscapeobservation.json".equals(path[2])) {
                return new LandscapeObservationResponder(landscapeFetcher.landscapeNamed(path[1]), spyIntermediary, prison);
            }
            
            if ("landscapes".equals(path[0]) && "addNote".equals(path[2])) {
                return new TargetNotationHandler(landscapeFetcher, spyIntermediary, tracker);
            }
            
            if ("landscapes".equals(path[0]) && "doh".equals(path[2])) {
                return new DohHandler(landscapeFetcher.landscapeNamed(path[1]), prison, tracker);
            }
        }
        
        return new NotFoundResponder();
    }
}