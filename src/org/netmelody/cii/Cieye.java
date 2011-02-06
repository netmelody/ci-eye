package org.netmelody.cii;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.netmelody.cii.persistence.State;
import org.netmelody.cii.response.FileResponder;
import org.netmelody.cii.response.JsonResponder;
import org.netmelody.cii.response.JsonResponseBuilder;
import org.netmelody.cii.response.json.CreateLandscapeResponseBuilder;
import org.netmelody.cii.response.json.LandscapeListResponseBuilder;
import org.netmelody.cii.response.json.TargetListResponseBuilder;
import org.netmelody.cii.witness.DummyWitness;
import org.simpleframework.http.Address;
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
            
            if ("createLandscape.json".equals(name)) {
                return new CreateLandscapeResponseBuilder(state);
            }
            
            return new TargetListResponseBuilder(new DummyWitness());
        }
    }
}
