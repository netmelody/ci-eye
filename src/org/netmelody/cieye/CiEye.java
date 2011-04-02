package org.netmelody.cieye;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.netmelody.cieye.server.configuration.State;
import org.netmelody.cieye.server.response.CiEyeResourceEngine;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.resource.ResourceContainer;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public final class CiEye {
    
    private final State state = new State();
    private final Container container = new ResourceContainer(new CiEyeResourceEngine(state));
    private final Connection connection;
    private final InetSocketAddress address;

    public static void main(String[] args) throws Exception {
        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        }
        catch (Exception e) {
            System.out.println("Usage: CiEye port");
        }
        if (port > 65535 || port < 0) {
            port = 0;
        }
        
        new CiEye(port).start();
    }
    
    public CiEye(int port) throws IOException {
        connection = new SocketConnection(container);
        address = new InetSocketAddress(port);
    }

    private void start() throws IOException {
        final SocketAddress socketAddress = connection.connect(address);
        
        if (socketAddress instanceof InetSocketAddress) {
            System.out.println("Starting Ci-Eye server on port: " + ((InetSocketAddress)socketAddress).getPort());
            return;
        }
        
        System.out.println("Starting Ci-Eye server on: " + socketAddress.toString());
    }
}
