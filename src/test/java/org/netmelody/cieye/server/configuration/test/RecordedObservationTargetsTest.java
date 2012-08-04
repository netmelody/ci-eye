package org.netmelody.cieye.server.configuration.test;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.server.configuration.RecordedObservationTargets;
import org.netmelody.cieye.server.configuration.SettingsFile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public final class RecordedObservationTargetsTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test public void
    readsLandscapesFromFile() throws Exception {
        final File views = testFolder.newFile("views.txt");
        FileUtils.copyInputStreamToFile(RecordedObservationTargetsTest.class.getResourceAsStream("testviews.txt"), views);
        final RecordedObservationTargets targets = new RecordedObservationTargets(new SettingsFile(views));
        assertThat(targets.landscapes().landscapeNamed("Landscape- 1").name(), is("Landscape- 1"));
        assertThat(targets.landscapes().landscapeNamed("Landscape- 2").name(), is("Landscape- 2"));
    }
    
    @Test public void
    populatesLandscapeWithFeatures() throws Exception {
        final File views = testFolder.newFile("views.txt");
        FileUtils.copyInputStreamToFile(RecordedObservationTargetsTest.class.getResourceAsStream("testviews.txt"), views);
        final RecordedObservationTargets targets = new RecordedObservationTargets(new SettingsFile(views));
        final Collection<Feature> features = targets.landscapes().landscapeNamed("Landscape- 2").features();
        assertThat(features, contains(new Feature("Jenkins 1", "http://jenkinsurl", new CiServerType("JENKINS")),
                                      new Feature("Hudson 1", "http://hudsonurl", new CiServerType("HUDSON")),
                                      new Feature("TeamCity 1", "http://teamcityurl", new CiServerType("TEAMCITY")),
                                      new Feature("", "http://allurl", new CiServerType("JENKINS")),
                                      new Feature("TeamCity 2", "http://tcsecure", new CiServerType("TEAMCITY"), "user", "pass")));
    }

    @Test public void
    successfullyProcessesTemplateForViews() throws Exception {
        final File views = testFolder.newFile("views.txt");
        FileUtils.copyInputStreamToFile(RecordedObservationTargets.class.getResourceAsStream("templates/views.txt.template"), views);
        final RecordedObservationTargets targets = new RecordedObservationTargets(new SettingsFile(views));

        assertThat(targets.landscapes().landscapeNamed("CI-eye Demo"), is(not(nullValue())));
        assertThat(targets.landscapes().landscapeNamed("Public Live"), is(not(nullValue())));
        
        final Collection<Feature> features = targets.landscapes().landscapeNamed("Public Live").features();
        assertThat(features, contains(new Feature("Jenkins core", "http://ci.jenkins-ci.org", new CiServerType("JENKINS")),
                                      new Feature("Main (trunk, branches, and alternative builds)", "http://hudson.magnolia-cms.com", new CiServerType("HUDSON")),
                                      new Feature("Apache Ivy", "http://teamcity.jetbrains.com", new CiServerType("TEAMCITY")),
                                      new Feature("CI-Eye", "http://teamcity.codebetter.com", new CiServerType("TEAMCITY"))));
    }
}
