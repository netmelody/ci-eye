package org.netmelody.cieye.server.observation;

import static java.lang.Boolean.TRUE;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.Map;

import org.netmelody.cieye.server.CiEyeNewVersionChecker;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public final class GovernmentReport implements CiEyeNewVersionChecker {

    private final CiEyeNewVersionChecker checker;
    
    private final Map<Boolean, String> report = new MapMaker()
                                                    .expireAfterWrite(10, MINUTES)
                                                    .makeComputingMap(fetchReport());

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
        return report.get(TRUE);
    }

}
