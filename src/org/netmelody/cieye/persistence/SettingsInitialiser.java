package org.netmelody.cieye.persistence;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

public final class SettingsInitialiser {
    
    private final File homeDir;
    private final File viewsFile;
    private final File picturesFile;
    private final File picturesDir;

    public SettingsInitialiser() {
        this(new File(FileUtils.getUserDirectory(), ".ci-eye"));
    }
    
    public SettingsInitialiser(File directory) {
        this.homeDir = directory;
        this.viewsFile = new File(homeDir, "views.txt");
        this.picturesFile = new File(homeDir, "pictures.txt");
        this.picturesDir = new File(homeDir, "pictures");
        
        try {
            startLogger();
            terraform();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialise ci-eye server", e);
        }
    }

    private void startLogger() throws IOException {
        final File loggingPropertiesFile = new File(homeDir, "logging.properties");
        if (!loggingPropertiesFile.exists()) {
            FileUtils.copyInputStreamToFile(resource("logging.properties.template"), loggingPropertiesFile);
            new File(homeDir, "logs").mkdir();
        }
        System.setProperty("java.util.logging.config.file", loggingPropertiesFile.getCanonicalPath());
    }

    private void terraform() throws IOException {
        if (!viewsFile.exists()) {
            FileUtils.copyInputStreamToFile(resource("views.txt.template"), viewsFile);
        }
        
        if (!picturesFile.exists()) {
            FileUtils.copyInputStreamToFile(resource("pictures.txt.template"), picturesFile);
        }
        
        if (!picturesDir.exists()) {
            FileUtils.copyInputStreamToFile(resource("picture1.png.template"), new File(picturesDir, "vlad.png"));
            FileUtils.copyInputStreamToFile(resource("picture2.png.template"), new File(picturesDir, "stupid.png"));
        }
    }

    private InputStream resource(String named) {
        return SettingsInitialiser.class.getClassLoader().getResourceAsStream(named);
    }

    public File viewsFile() {
        return viewsFile;
    }

    public File picturesFile() {
        return picturesFile;
    }

    public File pictureNamed(String name) {
        return new File(picturesDir, name);
    }

    public String settingsLocation() {
        try {
            return homeDir.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to get settings directory", e);
        }
    }
}
