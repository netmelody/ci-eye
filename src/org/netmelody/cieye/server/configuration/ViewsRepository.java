package org.netmelody.cieye.server.configuration;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.LandscapeGroup;

public final class ViewsRepository {

    private static final Pattern LANDSCAPE_NAME_REGEX = Pattern.compile("^\\s*\\[(.*)\\]\\s*$");
    private static final Pattern FEATURE_REGEX = Pattern.compile("^(.*?)\\|(.*?)\\|(.*?)$");

    private final SettingsFile viewsFile;
    
    public ViewsRepository(SettingsFile viewsFile) {
        this.viewsFile = viewsFile;
    }
    
    public LandscapeGroup landscapes() {
        final List<Landscape> landscapes = extractLandscapeFrom(viewsFile.readContent());
        
        if (landscapes.isEmpty()) {
            return new LandscapeGroup(newArrayList(new Landscape("Ci-eye Demo", new Feature("My Product", "", new CiServerType("DEMO")))));
        }
        
        return new LandscapeGroup(landscapes);
    }

    private static List<Landscape> extractLandscapeFrom(List<String> content) {
        final List<Landscape> result = new ArrayList<Landscape>();
        
        String landscapeName = "";
        List<Feature> features = new ArrayList<Feature>();
        
        for (String line : content) {
            Matcher landscapeMatcher = LANDSCAPE_NAME_REGEX.matcher(line);
            if (landscapeMatcher.matches()) {
                if (landscapeName.length() > 0) {
                    result.add(new Landscape(landscapeName, features.toArray(new Feature[features.size()])));
                }
                landscapeName = landscapeMatcher.group(1);
                features.clear();
                continue;
            }
            
            Matcher featureMatcher = FEATURE_REGEX.matcher(line);
            if (featureMatcher.matches()) {
                features.add(new Feature(featureMatcher.group(3),
                                         featureMatcher.group(2),
                                         new CiServerType(featureMatcher.group(1))));
            }
        }
        
        if (landscapeName.length() > 0) {
            result.add(new Landscape(landscapeName, features.toArray(new Feature[features.size()])));
        }
        
        return result;
    }

}
