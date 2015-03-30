package org.netmelody.cieye.core.domain;

import com.google.common.base.Optional;

public final class TargetDigest extends Target {

    public TargetDigest(String id, String webUrl, Optional<String> featureName, String name, Status status) {
        super(id, webUrl, featureName, name, status);
    }
}
