package org.netmelody.cieye.core.utility;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Irritables {

    private Irritables() { }

    public static <T> Iterable<List<T>> partition(final Iterable<T> iterable, final Predicate<T> splicer) {
        checkNotNull(iterable);
        checkNotNull(splicer);
        return new Iterable<List<T>>() {
            @Override
            public Iterator<List<T>> iterator() {
                return Irritators.partition(iterable.iterator(), splicer);
            }
            @Override public String toString() {
                return Iterables.toString(this);
            }
        };
    }
}
