package org.netmelody.cii.response;

import org.netmelody.cii.persistence.State;
import org.netmelody.cii.response.json.LandscapeListResponseBuilder;
import org.netmelody.cii.response.json.LandscapeObservationResponseBuilder;
import org.netmelody.cii.response.json.SettingsLocationResponseBuilder;
import org.netmelody.cii.witness.DefaultWitnessProvider;
import org.netmelody.cii.witness.WitnessProvider;
import org.simpleframework.http.Address;
import org.simpleframework.http.Path;
import org.simpleframework.http.resource.Resource;
import org.simpleframework.http.resource.ResourceEngine;

public final class CiEyeResourceEngine implements ResourceEngine {
    private final State state;
    private final WitnessProvider witnessProvider;

    public CiEyeResourceEngine(State state) {
        this.state = state;
        this.witnessProvider = new DefaultWitnessProvider(state);
    }
    
    @Override
    public Resource resolve(Address target) {
        if ("json".equals(target.getPath().getExtension())) {
            return new JsonResponder(jsonResponseBuilderFor(target));
        }
        if ((target.getPath().getSegments().length > 0) && "/pictures".equals(target.getPath().getPath(0, 1))) {
            return new PictureResponder(state, target.getPath());
        }
        return new FileResponder(target.getPath());
    }

    private JsonResponseBuilder jsonResponseBuilderFor(Address target) {
        final String name = target.getPath().getName();
        
        if ("landscapeobservation.json".equals(name)) {
            return new LandscapeObservationResponseBuilder(state, witnessProvider);
        }
        
        if ("landscapelist.json".equals(name)) {
            return new LandscapeListResponseBuilder(state);
        }
        
        if ("settingslocation.json".equals(name)) {
            return new SettingsLocationResponseBuilder(state);
        }
        
        return new JsonResponseBuilder() {
            @Override
            public JsonResponse buildResponse(Path path, String requestContent) {
                return new JsonResponse("");
            }
        };
    }
}