package org.netmelody.cii;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.netmelody.cii.persistence.State;
import org.netmelody.cii.response.FileResponder;
import org.netmelody.cii.response.JsonResponder;
import org.netmelody.cii.response.JsonResponse;
import org.netmelody.cii.response.JsonResponseBuilder;
import org.netmelody.cii.response.json.CreateLandscapeResponseBuilder;
import org.netmelody.cii.response.json.LandscapeListResponseBuilder;
import org.netmelody.cii.response.json.LandscapeObservationResponseBuilder;
import org.netmelody.cii.response.json.LandscapeResponseBuilder;
import org.netmelody.cii.witness.DefaultWitnessProvider;
import org.netmelody.cii.witness.WitnessProvider;
import org.simpleframework.http.Address;
import org.simpleframework.http.Path;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.resource.Resource;
import org.simpleframework.http.resource.ResourceContainer;
import org.simpleframework.http.resource.ResourceEngine;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public final class Cieye {
    
    public static void main(String[] list) throws Exception {
        Container container = new ResourceContainer(new CiEyeResourceEngine());
        Connection connection = new SocketConnection(container);
        SocketAddress address = new InetSocketAddress(8888);

        connection.connect(address);
    }

    private static final class CiEyeResourceEngine implements ResourceEngine {
        private final State state = new State();
        private final WitnessProvider witnessProvider = new DefaultWitnessProvider(state);

        @Override
        public Resource resolve(Address target) {
            if ("json".equals(target.getPath().getExtension())) {
                return new JsonResponder(jsonResponseBuilderFor(target));
            }
            return new FileResponder(target.getPath());
        }

        private JsonResponseBuilder jsonResponseBuilderFor(Address target) {
            final String name = target.getPath().getName();
            
            if ("landscapelist.json".equals(name)) {
                return new LandscapeListResponseBuilder(state);
            }
            
            if ("landscape.json".equals(name)) {
                return new LandscapeResponseBuilder(state);
            }
            
            if ("createLandscape.json".equals(name)) {
                return new CreateLandscapeResponseBuilder(state);
            }
            
            if ("landscapeobservation.json".endsWith(name)) {
                return new LandscapeObservationResponseBuilder(state, witnessProvider);
            }
            
            return new JsonResponseBuilder() {
                @Override
                public JsonResponse buildResponse(Path path, String requestContent) {
                    return new JsonResponse("");
                }
            };
        }
    }
}
