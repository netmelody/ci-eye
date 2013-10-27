package org.netmelody.cieye.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.server.configuration.ServerConfiguration;
import org.netmelody.cieye.server.observation.GovernmentReport;
import org.netmelody.cieye.server.observation.GovernmentWatchdog;
import org.netmelody.cieye.server.observation.IntelligenceAgency;
import org.netmelody.cieye.server.observation.protocol.JsonRestRequesterBuilder;
import org.netmelody.cieye.server.response.CachedRequestOriginTracker;
import org.netmelody.cieye.server.response.CiEyeResourceEngine;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.resource.ResourceContainer;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public final class CiEyeServer {

    private final ServerConfiguration agency = new ServerConfiguration();
    private final CommunicationNetwork network = new JsonRestRequesterBuilder();
    private final IntelligenceAgency intelligenceAgency = IntelligenceAgency.create(network, 
                                                                                    agency.detective(), 
                                                                                    agency.foreignAgents());
    private final Container container =
        new ResourceContainer(new CiEyeResourceEngine(agency.observationTargetDirectory(),
                                                      agency.album(),
                                                      agency.information(),
                                                      new CachedRequestOriginTracker(agency.detective()),
                                                      intelligenceAgency,
                                                      new GovernmentReport(new GovernmentWatchdog(network))));
    private final Connection connection;
    private final InetSocketAddress address;
    
    public CiEyeServer(int port) throws IOException {
        connection = new SocketConnection(container);
        address = new InetSocketAddress(port);
    }

    public void start() throws IOException {
        final SocketAddress socketAddress = connection.connect(address);
        
        if (socketAddress instanceof InetSocketAddress) {
            System.out.format("Starting CI-Eye server on http://localhost:%d", ((InetSocketAddress)socketAddress).getPort());
            return;
        }
        
        System.out.format("Starting CI-Eye server on: %s", socketAddress.toString());
    }
}