package org.netmelody.cieye.core.utility;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class ProjectAbbreviatorTest {

    @Test
    public void doesNotAbbreviateIfNoSpaceInName() throws Throwable {
        Assert.assertThat("myProjectName", equalTo(new ProjectAbbreviator().abbreviate("myProjectName")));
    }

    @Test
    public void abbreviatesToFirstLettersOfWordsIfThereAreSpaces() throws Throwable {
        Assert.assertThat("MpN", equalTo(new ProjectAbbreviator().abbreviate("My project Name")));
    }
}