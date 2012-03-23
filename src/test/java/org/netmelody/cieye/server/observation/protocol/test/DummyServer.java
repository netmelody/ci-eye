package org.netmelody.cieye.server.observation.protocol.test;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public final class DummyServer {
    
    private final int port;
    private final Connection connection;
    private String responseText = "";
    private int code = 200;

    public DummyServer() {
        try {
            connection = new SocketConnection(new Container(){
                @Override public void handle(Request request, Response response) {
                    try {
                        handleSafely(response);
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                }
            });
            
            final SocketAddress address = new InetSocketAddress(0);
            final SocketAddress socketAddress = connection.connect(address);
            this.port = ((InetSocketAddress)socketAddress).getPort();
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void respondWith(String responseText) {
        this.responseText = responseText;
    }

    public void respondWithStatusCode(int code) {
        this.code = code;
    }

    public int port() {
        return this.port;
    }
    
    public void close() {
        try {
            this.connection.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    private void handleSafely(Response response) throws IOException {
        PrintStream body = response.getPrintStream();
        long time = System.currentTimeMillis();

        response.set("Content-Type", "text/plain");
        response.set("Server", "HelloWorld/1.0 (Simple 4.0)");
        response.setDate("Date", time);
        response.setDate("Last-Modified", time);
        response.setCode(code );

        body.println(responseText);
        body.close();
    }
}