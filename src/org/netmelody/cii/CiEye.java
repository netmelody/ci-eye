package org.netmelody.cii;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.netmelody.cii.persistence.State;
import org.netmelody.cii.response.CiEyeResourceEngine;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.resource.ResourceContainer;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public final class CiEye {
    
    private final State state = new State();
    private final Container container = new ResourceContainer(new CiEyeResourceEngine(state));
    private final Connection connection;
    private final SocketAddress address;

    public static void main(String[] args) throws Exception {
        new CiEye(8888).start();
    }
    
    public CiEye(int port) throws IOException {
        connection = new SocketConnection(container);
        address = new InetSocketAddress(port);
    }

    private void start() throws IOException {
        connection.connect(address);
    }
}
