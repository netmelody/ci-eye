package org.netmelody.cieye;

import org.netmelody.cieye.server.CiEyeServer;

public final class CiEye {
    
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
        
        new CiEyeServer(port).start();
    }
}
