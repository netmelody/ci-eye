package org.netmelody.cii.persistence;

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
            terraform();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialise ci-eye server", e);
        }
    }

    private void terraform() throws IOException {
        if (!viewsFile.exists()) {
            FileUtils.copyInputStreamToFile(resource("views.txt.template"), viewsFile);
        }
        
        if (!picturesFile.exists()) {
            FileUtils.copyInputStreamToFile(resource("pictures.txt.template"), picturesFile);
        }
        
        if (!picturesDir.exists()) {
            FileUtils.copyInputStreamToFile(resource("picture.png.template"), new File(picturesDir, "vlad.png"));
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
}
