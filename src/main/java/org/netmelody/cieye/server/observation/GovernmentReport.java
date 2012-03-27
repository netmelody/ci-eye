package org.netmelody.cieye.server.observation;

import java.util.concurrent.ExecutionException;

import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.logging.Logbook;
import org.netmelody.cieye.server.CiEyeNewVersionChecker;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import static java.lang.Boolean.TRUE;
import static java.util.concurrent.TimeUnit.MINUTES;

public final class GovernmentReport implements CiEyeNewVersionChecker {

    private static final Logbook LOG = LogKeeper.logbookFor(GovernmentReport.class);
    
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
        try {
            return report.get(TRUE);
        } catch (ExecutionException e) {
            LOG.error("Unable to determine latest version of CI-Eye", e);
            return "";
        }
    }

}
