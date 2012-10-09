package org.netmelody.cieye.server.response.responder;

import java.io.IOException;

import org.netmelody.cieye.server.CiEyeNewVersionChecker;
import org.netmelody.cieye.server.CiEyeServerInformationFetcher;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.netmelody.cieye.server.response.JsonTranslator;
import org.simpleframework.http.Request;

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
    public CiEyeResponse respond(Request request) throws IOException {
        final VersionInformation versionInformation = new VersionInformation(configurationFetcher.getVersion(),
                                                                             updateChecker.getLatestVersion());
        
        return CiEyeResponse.withJson(new JsonTranslator().toJson(versionInformation)).expiringInMillis(10000L);
    }

}
