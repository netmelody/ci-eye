package org.netmelody.cieye.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.netmelody.cieye.server.configuration.State;
import org.netmelody.cieye.server.response.CiEyeResourceEngine;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.resource.ResourceContainer;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public final class CiEyeServer {

    private final State state = new State();
    private final Container container = new ResourceContainer(new CiEyeResourceEngine(state));
    private final Connection connection;
    private final InetSocketAddress address;
    
    public CiEyeServer(int port) throws IOException {
        connection = new SocketConnection(container);
        address = new InetSocketAddress(port);
    }

    public void start() throws IOException {
        final SocketAddress socketAddress = connection.connect(address);
        
        if (socketAddress instanceof InetSocketAddress) {
            System.out.println("Starting Ci-Eye server on port: " + ((InetSocketAddress)socketAddress).getPort());
            return;
        }
        
        System.out.println("Starting Ci-Eye server on: " + socketAddress.toString());
    }
}
