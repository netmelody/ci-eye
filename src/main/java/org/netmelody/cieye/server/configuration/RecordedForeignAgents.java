package org.netmelody.cieye.server.configuration;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.logging.Logbook;
import org.netmelody.cieye.core.observation.ForeignAgents;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public final class RecordedForeignAgents implements ForeignAgents {
    private final static Logbook LOGBOOK = LogKeeper.logbookFor(RecordedForeignAgents.class);
    
    private final File pluginDirectory;

    public RecordedForeignAgents(File pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
    }

    @Override
    public Set<ForeignAgents.CallingCard> search() {
        File[] jarFiles = pluginDirectory.listFiles((FileFilter)new SuffixFileFilter(".jar"));
        
        return jarFiles == null ? Collections.<ForeignAgents.CallingCard>emptySet() : makeCallingCardsFrom(jarFiles);
    }
    
    private Set<ForeignAgents.CallingCard> makeCallingCardsFrom(File[] jarFiles) {
        Set<URL> urls = Sets.newHashSet();
        Set<Throwable> problems = Sets.newHashSet();
        for (File file : jarFiles) {
            try {
                urls.add(file.toURI().toURL());
            } catch (MalformedURLException e) {
                problems.add(e);
            }
        }
        
        if (!problems.isEmpty()) {
            System.out.printf("Found [%d] problems obtaining plugin jars. See logs for more details.%n", problems.size());
            for (Throwable throwable : problems) {
                LOGBOOK.error("Error loading plugin.", throwable);
            }
        }
        
        return ImmutableSet.copyOf(Iterables.transform(urls, ForeignAgents.CallingCard.FROM_URL));
    }

}
