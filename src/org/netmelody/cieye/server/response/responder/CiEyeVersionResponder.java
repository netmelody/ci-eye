package org.netmelody.cieye.server.response.responder;

import java.io.IOException;

import org.netmelody.cieye.server.CiEyeServerInformationFetcher;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.JsonTranslator;
import org.simpleframework.http.Response;

public final class CiEyeVersionResponder implements CiEyeResponder {
    
    public static final class VersionInformation {
        @SuppressWarnings("unused") private final String currentServerVersion;
        private VersionInformation(String currentServerVersion) {
            this.currentServerVersion = currentServerVersion;
        }
    }

    private final CiEyeServerInformationFetcher configurationFetcher;

    public CiEyeVersionResponder(CiEyeServerInformationFetcher configurationFetcher) {
        this.configurationFetcher = configurationFetcher;
    }

    @Override
    public void writeTo(Response response) throws IOException {
        response.set("Content-Type", "application/json");
        response.setDate("Expires", System.currentTimeMillis() + 10000L);
        
        final VersionInformation versionInformation = new VersionInformation(configurationFetcher.getVersion());
        response.getPrintStream().println(new JsonTranslator().toJson(versionInformation));
    }
    
}
