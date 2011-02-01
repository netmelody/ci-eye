package org.netmelody.cii;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.netmelody.cii.response.FileResponder;
import org.simpleframework.http.Address;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.resource.FileContext;
import org.simpleframework.http.resource.Resource;
import org.simpleframework.http.resource.ResourceContainer;
import org.simpleframework.http.resource.ResourceEngine;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public final class Cieye {

    public static void main(String[] list) throws Exception {
        Container container = new ResourceContainer(new StaticResourceEngine());
        Connection connection = new SocketConnection(container);
        SocketAddress address = new InetSocketAddress(8080);

        connection.connect(address);
    }

    private static final class StaticResourceEngine implements ResourceEngine {
        private final FileContext fileContext = new FileContext();

        @Override
        public Resource resolve(Address target) {
            return new FileResponder(fileContext.getIndex(target.getPath().getPath()));
        }
    }
}
