package org.netmelody.cieye.core.utility.test;

import java.util.List;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.google.common.base.Predicate;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.netmelody.cieye.core.utility.Irritables.partition;

public final class IrritablesTest {

    @Test
    public void canPartitionAListWithAPredicate() {
        assertThat(partition(newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), isPrime()),
                   Matchers.<List<?>>contains(newArrayList(1),
                                              newArrayList(2),
                                              newArrayList(3, 4),
                                              newArrayList(5, 6),
                                              newArrayList(7, 8, 9, 10)));
    }
    
    @Test
    public void canPartitionAListWithAPredicateWhenThatListBeginsAndEndsWithAMatch() {
        assertThat(partition(newArrayList(2, 3, 4, 5, 6, 7, 8, 9, 10, 11), isPrime()),
                   Matchers.<List<?>>contains(newArrayList(),
                                              newArrayList(2),
                                              newArrayList(3, 4),
                                              newArrayList(5, 6),
                                              newArrayList(7, 8, 9, 10),
                                              newArrayList(11)));
    }
    
    private Predicate<Integer> isPrime() {
        return new Predicate<Integer>() {
            private final Set<Integer> primes = newHashSet(2, 3, 5, 7, 11);
            @Override
            public boolean apply(Integer input) {
                return primes.contains(input);
            }
        };
    }

}
