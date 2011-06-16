package org.netmelody.cieye.server.configuration.test;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.netmelody.cieye.server.configuration.SettingsInitialiser;

public final class SettingsInitialiserTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    

    @Test public void
    createsConfigurationFiles() {
        final File folder = testFolder.newFolder("test");
        new SettingsInitialiser(folder);
    }
}
