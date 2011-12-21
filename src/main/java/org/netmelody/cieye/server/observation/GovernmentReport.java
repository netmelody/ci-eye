package org.netmelody.cieye.server.observation;

import static java.lang.Boolean.TRUE;
import static java.util.concurrent.TimeUnit.MINUTES;

import org.netmelody.cieye.server.CiEyeNewVersionChecker;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public final class GovernmentReport implements CiEyeNewVersionChecker {

    private final CiEyeNewVersionChecker checker;
    
    private final LoadingCache<Boolean, String> report = CacheBuilder.newBuilder()
                                                             .expireAfterWrite(10, MINUTES)
                                                             .build(CacheLoader.from(fetchReport()));

    public GovernmentReport(CiEyeNewVersionChecker checker) {
        this.checker = checker;
    }
    
    private Function<Boolean, String> fetchReport() {
        return new Function<Boolean, String>() {
            @Override
            public String apply(Boolean input) {
                return checker.getLatestVersion();
            }
        };
    }

    @Override
    public String getLatestVersion() {
        return report.getUnchecked(TRUE);
    }

}
