package org.netmelody.cii;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.netmelody.cii.persistence.State;
import org.netmelody.cii.response.CiEyeResourceEngine;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.resource.ResourceContainer;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public final class CiEye {
    
    public static void main(String[] list) throws Exception {
        final State state = new State();
        final Container container = new ResourceContainer(new CiEyeResourceEngine(state));
        final Connection connection = new SocketConnection(container);
        final SocketAddress address = new InetSocketAddress(8888);
        
        connection.connect(address);
    }
}
