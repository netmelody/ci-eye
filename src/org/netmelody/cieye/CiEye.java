/*
 * Copyright (C) 2011 Tom Denley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
