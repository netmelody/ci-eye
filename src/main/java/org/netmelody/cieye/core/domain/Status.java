package org.netmelody.cieye.core.domain;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

public enum Status {
    GREEN, BROKEN, DISABLED, UNKNOWN, UNDER_INVESTIGATION;
    
    public static final Ordering<Status> RANK = new Ordering<Status>() {
        private final List<Status> order = ImmutableList.of(DISABLED, GREEN, UNKNOWN, UNDER_INVESTIGATION, BROKEN);
        @Override public int compare(Status left, Status right) {
            final int leftRank = order.indexOf(left);
            final int rightRank = order.indexOf(right);
            return (leftRank < rightRank ? -1 : (leftRank == rightRank ? 0 : 1));
        }
    };
}
