package org.netmelody.cieye.server.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.netmelody.cieye.core.domain.Sponsor;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;

public final class RecordedKnownOffenders implements KnownOffendersDirectory, Refreshable {
    
    private static final Pattern PICTURE_FILENAME_REGEX = Pattern.compile("^\\s*\\[(.*)\\]\\s*$");

    private final SettingsFile picturesFile;
    private Map<String, Sponsor> userMap = new HashMap<String, Sponsor>();
    
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
    public List<Sponsor> search(String fingerprint) {
        final Collection<Sponsor> sponsors = new HashSet<Sponsor>();
        
        final String upperChangeText = fingerprint.toUpperCase();
        for (String keyword : userMap.keySet()) {
            if (upperChangeText.contains(keyword)) {
                sponsors.add(userMap.get(keyword));
            }
        }
        return new ArrayList<Sponsor>(sponsors);
    }
    
    private void loadPictureSettings() {
        userMap = extractPicuresFrom(picturesFile.readContent());
    }

    private static Map<String, Sponsor> extractPicuresFrom(List<String> content) {
        final Map<String, Sponsor> result = new HashMap<String, Sponsor>();
        final List<String> aliases = new ArrayList<String>();
        String pictureFilename = "";
        
        for (String line : content) {
            Matcher pictureFilenameMatcher = PICTURE_FILENAME_REGEX.matcher(line);
            if (pictureFilenameMatcher.matches()) {
                if (pictureFilename.length() != 0 && !aliases.isEmpty()) {
                    registerUser(result, aliases.get(0), "/pictures/" + pictureFilename, aliases.toArray(new String[aliases.size()]));
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
            registerUser(result, aliases.get(0), "/pictures/" + pictureFilename, aliases.toArray(new String[aliases.size()]));
        }
        return result;
    }
    
    private static void registerUser(Map<String, Sponsor> resultMap, String name, String pictureUrl, String... keywords) {
        final Sponsor user = new Sponsor(name, pictureUrl);
        resultMap.put(name.toUpperCase(), user);
        for (String keyword : keywords) {
            resultMap.put(keyword.toUpperCase(), user);
        }
    }
}
