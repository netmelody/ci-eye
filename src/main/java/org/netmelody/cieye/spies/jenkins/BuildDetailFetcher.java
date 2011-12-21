package org.netmelody.cieye.spies.jenkins;

import static com.google.common.cache.CacheLoader.from;

import java.util.concurrent.TimeUnit;

import org.netmelody.cieye.spies.jenkins.jsondomain.BuildDetail;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public final class BuildDetailFetcher {

    private final LoadingCache<String, BuildDetail> buildDetails;
    
    public BuildDetailFetcher(final JenkinsCommunicator communicator) {
        buildDetails =
            CacheBuilder.newBuilder()
                 .expireAfterWrite(2, TimeUnit.SECONDS)
                 .build(from(new Function<String, BuildDetail>() {
                                 public BuildDetail apply(String buildUrl) {
                                     return communicator.buildDetailsFor(buildUrl);
                                 }
                             }));
    }
    
    public BuildDetail detailsOf(String buildUrl) {
        return buildDetails.getUnchecked(buildUrl);
    }
}
