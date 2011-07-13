package org.netmelody.cieye.server.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class SettingsFile {

    private static final Log LOG = LogFactory.getLog(SettingsFile.class);
    
    private final File file;
    
    private Date lastReadDate = new Date(0L); 

    public SettingsFile(File file) {
        this.file = file;
    }
    
    public List<String> readContent() {
        if (! file.canRead()) {
            return new ArrayList<String>();
        }
        
        try {
            final List<String> result = FileUtils.readLines(this.file);
            lastReadDate = new Date();
            return result;
        } catch (IOException e) {
            LOG.error("failed to read settings file", e);
        }
        return new ArrayList<String>();
    }
    
    public boolean updateAvailable() {
        return FileUtils.isFileNewer(file, lastReadDate);
    }
    
    public SettingsFile newReference() {
        return new SettingsFile(this.file);
    }
}
