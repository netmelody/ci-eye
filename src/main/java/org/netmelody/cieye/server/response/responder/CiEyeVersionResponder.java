package org.netmelody.cieye.server.response.responder;

import java.io.IOException;

import org.netmelody.cieye.server.CiEyeNewVersionChecker;
import org.netmelody.cieye.server.CiEyeServerInformationFetcher;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.JsonTranslator;
import org.simpleframework.http.Response;

public final class CiEyeVersionResponder implements CiEyeResponder {
    
    public static final class VersionInformation {
        @SuppressWarnings("unused") private final String currentServerVersion;
        @SuppressWarnings("unused") private final String latestServerVersion;
        private VersionInformation(String currentServerVersion, String latestServerVersion) {
            this.currentServerVersion = currentServerVersion;
            this.latestServerVersion = latestServerVersion;
        }
    }

    private final CiEyeServerInformationFetcher configurationFetcher;
    private final CiEyeNewVersionChecker updateChecker;

    public CiEyeVersionResponder(CiEyeServerInformationFetcher configurationFetcher, CiEyeNewVersionChecker updateChecker) {
        this.configurationFetcher = configurationFetcher;
        this.updateChecker = updateChecker;
    }

    @Override
    public void writeTo(Response response) throws IOException {
        response.set("Content-Type", "application/json");
        response.setDate("Expires", System.currentTimeMillis() + 10000L);
        
        final VersionInformation versionInformation = new VersionInformation(configurationFetcher.getVersion(),
                                                                             updateChecker.getLatestVersion());
        
        response.getPrintStream().println(new JsonTranslator().toJson(versionInformation));
    }
    
}
