package org.netmelody.cieye.server.configuration.test;

import java.io.File;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.netmelody.cieye.server.configuration.SettingsInitialiser;

import static org.apache.commons.io.filefilter.TrueFileFilter.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;

public final class SettingsInitialiserTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    

    @Test public void
    createsConfigurationFiles() {
        final File folder = testFolder.newFolder("test");
        new SettingsInitialiser(folder);
        
        final String[] generatedFiles = folder.list(TRUE);
        Arrays.sort(generatedFiles);
        assertThat(generatedFiles, is(arrayContaining("logging.properties",
                                                      "logs",
                                                      "pictures",
                                                      "pictures.txt",
                                                      "plugins",
                                                      "views.txt")));
    }
    
    @Test public void
    generatesDemoPictureFiles() {
        final File folder = testFolder.newFolder("test");
        new SettingsInitialiser(folder);
        
        final String[] generatedFiles = new File(folder, "pictures").list(TRUE);
        Arrays.sort(generatedFiles);
        assertThat(generatedFiles, is(arrayContaining("all-green.gif",
                                                      "doh.png",
                                                      "stupid.png",
                                                      "vlad.png")));
    }
}