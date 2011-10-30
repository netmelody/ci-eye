package org.netmelody.cieye.core.utility;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.UnmodifiableIterator;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

public final class Irritators {

    private Irritators() { }

    public static <T> Iterable<List<T>> partition(final Iterable<T> iterable, final Predicate<T> splicer) {
        checkNotNull(iterable);
        checkNotNull(splicer);
        return new Iterable<List<T>>() {
            @Override
            public Iterator<List<T>> iterator() {
                return partition(iterable.iterator(), splicer);
            }
            @Override public String toString() {
                return Iterables.toString(this);
            }
        };
    }
    
    public static <T> UnmodifiableIterator<List<T>> partition(final Iterator<T> iterator, final Predicate<T> splicer) {
        checkNotNull(iterator);
        checkNotNull(splicer);
        
        return new UnmodifiableIterator<List<T>>() {
            private List<T> nextResult = newArrayList();

            @Override
            public boolean hasNext() {
                return iterator.hasNext() || !nextResult.isEmpty();
            }
            
            @Override
            public List<T> next() {
                if (!hasNext()) {
                    if (nextResult.isEmpty()) {
                        throw new NoSuchElementException();
                    }
                    
                    List<T> result = unmodifiableList(nextResult);
                    nextResult = newArrayList();
                    return result;
                }
                
                final List<T> result = nextResult;
                nextResult  = newArrayList();
                for (; iterator.hasNext(); ) {
                    final T next = iterator.next();
                    if (splicer.apply(next)) {
                        nextResult.add(next);
                        return unmodifiableList(result);
                    }
                    result.add(next);
                }
                return unmodifiableList(result);
            }
        };
    }
}
