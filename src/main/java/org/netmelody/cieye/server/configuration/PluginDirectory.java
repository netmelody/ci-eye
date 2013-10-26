package org.netmelody.cieye.server.configuration;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

public final class PluginDirectory {

    private final File directory;
    private Date lastReadDate = new Date(0L); 

    public PluginDirectory(File directory) {
        this.directory = directory;
    }

    public Iterable<File> jars() {
        lastReadDate = new Date();
        File[] jarFiles = directory.listFiles((FileFilter)new SuffixFileFilter(".jar"));
        return jarFiles == null ? Collections.<File>emptySet() : Arrays.asList(jarFiles);
    }
    
    public boolean updateAvailable() {
        return FileUtils.isFileNewer(directory, lastReadDate);
    }
    
}
