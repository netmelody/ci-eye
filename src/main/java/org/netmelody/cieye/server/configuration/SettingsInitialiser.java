package org.netmelody.cieye.server.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.ImmutableList;

public final class SettingsInitialiser {
    
    private final File homeDir;
    private final File viewsFile;
    private final File picturesFile;
    private final File picturesDir;
    private final File pluginsDir;

    public SettingsInitialiser() {
        this(new File(FileUtils.getUserDirectory(), ".ci-eye"));
    }
    
    public SettingsInitialiser(File directory) {
        this.homeDir = directory;
        this.viewsFile = new File(homeDir, "views.txt");
        this.picturesFile = new File(homeDir, "pictures.txt");
        this.picturesDir = new File(homeDir, "pictures");
        this.pluginsDir = new File(homeDir, "plugins");
        
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
            initialiseLogging(loggingPropertiesFile);
        }
        System.setProperty("java.util.logging.config.file", loggingPropertiesFile.getCanonicalPath());
    }

    public void initialiseLogging(final File loggingPropertiesFile) throws IOException {
        FileUtils.copyInputStreamToFile(resource("logging.properties.template"), loggingPropertiesFile);
        
        final String logsDirName = "logs";
        final boolean createdLogsDir = new File(homeDir, logsDirName).mkdir();
        if (!createdLogsDir) {
            System.err.println("Failed to create logs directory at" + homeDir.getPath() + File.pathSeparator + logsDirName);
        }
    }

    private void terraform() throws IOException {
        if (!viewsFile.exists()) {
            FileUtils.copyInputStreamToFile(resource("views.txt.template"), viewsFile);
        }
        
        if (!picturesFile.exists()) {
            FileUtils.copyInputStreamToFile(resource("pictures.txt.template"), picturesFile);
            includePicture("vlad.png", "picture1.png.template");
            includePicture("stupid.png", "picture2.png.template");
        }
        
        if (!pluginsDir.exists()) {
            pluginsDir.mkdir();
        }
        
        ensureSpecialPicturePresent("-doh-", "doh.png", "picture3.png.template");
        ensureSpecialPicturePresent("-all-green-", "all-green.gif", "picture4.gif.template");
    }

    private void ensureSpecialPicturePresent(String targetHandle, String targetFileName, String templateName) throws IOException {
        if (FileUtils.readFileToString(picturesFile).contains(targetHandle)) {
            return;
        }
        
        includePicture(targetFileName, templateName);
        FileUtils.writeLines(picturesFile, ImmutableList.of("", "[" + targetFileName + "]", targetHandle), true);
    }

    private void includePicture(String targetName, String templateName) throws IOException {
        final File target = new File(picturesDir, targetName);
        
        if (!target.exists()) {
            FileUtils.copyInputStreamToFile(resource(templateName), target);
        }
    }

    private InputStream resource(String name) {
        return SettingsInitialiser.class.getResourceAsStream("templates/" + name);
    }

    public SettingsFile viewsFile() {
        return new SettingsFile(viewsFile);
    }

    public SettingsFile picturesFile() {
        return new SettingsFile(picturesFile);
    }

    public File picturesDirectory() {
        return picturesDir;
    }

    public String settingsLocation() {
        try {
            return homeDir.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to get settings directory", e);
        }
    }

    public File pluginDirectory() {
        return pluginsDir;
    }
}
