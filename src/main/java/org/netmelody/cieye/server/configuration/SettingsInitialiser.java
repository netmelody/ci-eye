package org.netmelody.cieye.server.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import static com.google.common.base.Predicates.equalTo;

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
        }
        
        placePicture("picture1.png.template", "vlad.png");
        placePicture("picture2.png.template", "stupid.png");
        placePicture("picture3.png.template", "doh.png");
        placePicture("picture4.gif.template", "all-green.gif");
    }

    private void placePicture(String templateName, String targetName) throws IOException {
        final File target = new File(picturesDir, targetName);
        
        if (!target.exists()) {
            FileUtils.copyInputStreamToFile(resource(templateName), target);
        }
        
        if (FileUtils.readFileToString(picturesFile).contains(targetName)) {
            return;
        }
        
        final List<String> template = IOUtils.readLines(resource("pictures.txt.template"));
        final String header = "[" + targetName + "]";

        int index = Iterables.indexOf(template, equalTo(header)) + 1;
        if (index == 0) {
            return;
        }
        
        final List<String> lines = Lists.newArrayList();
        lines.add("");
        lines.add(header);
            
        while (index < template.size() && !template.get(index).contains("[")) {
            lines.add(template.get(index));
            index += 1;
        }
        FileUtils.writeLines(picturesFile, lines, true);
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
}
