package org.netmelody.cieye.server.configuration.test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
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
    private RecordedObservationTargets targets;

    @Before
    public void createOffendersFile() throws IOException {
        final File views = testFolder.newFile("views.txt");
        FileUtils.copyInputStreamToFile(RecordedObservationTargets.class.getResourceAsStream("templates/views.txt.template"),
                                        views);
        
        targets = new RecordedObservationTargets(new SettingsFile(views));
    }
    
    @Test public void
    readsLandscapesFromFile() {
        assertThat(targets.landscapes().landscapeNamed("CI-eye Demo"), is(not(nullValue())));
        assertThat(targets.landscapes().landscapeNamed("Public Live"), is(not(nullValue())));
    }
    
    @Test public void
    populatesLandscapeWithFeatures() {
        final Collection<Feature> features = targets.landscapes().landscapeNamed("Public Live").features();
        assertThat(features, contains(new Feature("Jenkins core", "http://ci.jenkins-ci.org", new CiServerType("JENKINS")),
                                      new Feature("Main (trunk, branches, and alternative builds)", "http://hudson.magnolia-cms.com", new CiServerType("HUDSON")),
                                      new Feature("Gradle", "http://teamcity.jetbrains.com", new CiServerType("TEAMCITY")),
                                      new Feature("CI-Eye", "http://teamcity.codebetter.com", new CiServerType("TEAMCITY"))));
    }
}
