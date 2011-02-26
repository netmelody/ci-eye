package org.netmelody.cii;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.netmelody.cii.response.CiEyeResourceEngine;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.resource.ResourceContainer;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public final class CiEye {
    
    public static void main(String[] list) throws Exception {
        System.setProperty("java.util.logging.config.file", "logging.properties");
        Container container = new ResourceContainer(new CiEyeResourceEngine());
        Connection connection = new SocketConnection(container);
        SocketAddress address = new InetSocketAddress(8888);
        connection.connect(address);
    }
}
