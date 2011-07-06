package org.netmelody.cieye.server.configuration;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Maps.filterValues;
import static java.util.Collections.unmodifiableSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.netmelody.cieye.core.domain.Sponsor;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public final class RecordedKnownOffenders implements KnownOffendersDirectory, Refreshable {
    
    private static final Pattern PICTURE_FILENAME_REGEX = Pattern.compile("^\\s*\\[(.*)\\]\\s*$");

    private final SettingsFile picturesFile;
    private Map<Sponsor, Collection<Pattern>> userMap = new HashMap<Sponsor, Collection<Pattern>>();
    
    public RecordedKnownOffenders(SettingsFile picturesFile) {
        this.picturesFile = picturesFile;
        loadPictureSettings();
    }

    @Override
    public void refresh() {
        if (picturesFile.updateAvailable()) {
            loadPictureSettings();
        }
    }
    
    @Override
    public Set<Sponsor> search(String fingerprint) {
        return unmodifiableSet(filterValues(userMap, matching(fingerprint)).keySet());
    }
    
    private Predicate<Collection<Pattern>> matching(final String fingerprint) {
        return new Predicate<Collection<Pattern>>() {
            @Override
            public boolean apply(Collection<Pattern> fingers) {
                return any(fingers, leftPrint(fingerprint));
            }
        };
    }

    private Predicate<Pattern> leftPrint(final String fingerprint) {
        return new Predicate<Pattern>() {
            @Override
            public boolean apply(Pattern finger) {
                return finger.matcher(fingerprint).find();
            }
        };
    }

    private void loadPictureSettings() {
        userMap = extractPicuresFrom(picturesFile.readContent());
    }

    private static Map<Sponsor, Collection<Pattern>> extractPicuresFrom(List<String> content) {
        final Map<Sponsor, Collection<Pattern>> result = new HashMap<Sponsor, Collection<Pattern>>();
        final List<String> aliases = new ArrayList<String>();
        String pictureFilename = "";
        
        for (String line : content) {
            Matcher pictureFilenameMatcher = PICTURE_FILENAME_REGEX.matcher(line);
            if (pictureFilenameMatcher.matches()) {
                if (pictureFilename.length() != 0 && !aliases.isEmpty()) {
                    registerUser(result, aliases.get(0), "/pictures/" + pictureFilename, aliases);
                }
                pictureFilename = pictureFilenameMatcher.group(1);
                aliases.clear();
                continue;
            }
            
            if (line.trim().length() > 0) {
                aliases.add(line);
            }
        }
        
        if (pictureFilename.length() != 0 && !aliases.isEmpty()) {
            registerUser(result, aliases.get(0), "/pictures/" + pictureFilename, aliases);
        }
        return result;
    }
    
    private static void registerUser(Map<Sponsor, Collection<Pattern>> resultMap, String name, String pictureUrl, List<String> keywords) {
        resultMap.put(new Sponsor(name, pictureUrl), transform(new ArrayList<String>(keywords), toRegex()));
    }

    private static Function<String, Pattern> toRegex() {
        return new Function<String, Pattern>() {
            @Override
            public Pattern apply(String keyword) {
                return Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b", Pattern.CASE_INSENSITIVE);
            }
        };
    }
}
