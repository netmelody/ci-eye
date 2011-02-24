package org.netmelody.cii.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.netmelody.cii.domain.CiServerType;
import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.Landscape;
import org.netmelody.cii.domain.LandscapeGroup;

public final class ViewsRepository {

    private static final Pattern LANDSCAPE_NAME_REGEX = Pattern.compile("^\\s*\\[(.*)\\]\\s*$");
    private static final Pattern FEATURE_REGEX = Pattern.compile("^(.*?)\\|(.*?)\\|(.*?)$");
    
    private final File viewsFile;
    
    public ViewsRepository(File viewsFile) {
        this.viewsFile = viewsFile;
    }
    
    public LandscapeGroup landscapes() {
        if (!viewsFile.canRead()) {
            return LandscapeGroup.demo();
        }
        
        try {
            final List<String> content = FileUtils.readLines(viewsFile);
            return extractLandscapeFrom(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return LandscapeGroup.demo();
    }

    private static LandscapeGroup extractLandscapeFrom(List<String> content) {
        LandscapeGroup result = new LandscapeGroup();
        
        String landscapeName = "";
        List<Feature> features = new ArrayList<Feature>();
        
        for (String line : content) {
            Matcher landscapeMatcher = LANDSCAPE_NAME_REGEX.matcher(line);
            if (landscapeMatcher.matches()) {
                if (!landscapeName.isEmpty()) {
                    result = result.add(new Landscape(landscapeName, features.toArray(new Feature[features.size()])));
                }
                landscapeName = landscapeMatcher.group(1);
                features.clear();
                continue;
            }
            
            Matcher featureMatcher = FEATURE_REGEX.matcher(line);
            if (featureMatcher.matches()) {
                features.add(new Feature(featureMatcher.group(3), featureMatcher.group(2), CiServerType.from(featureMatcher.group(1))));
            }
        }
        
        if (!landscapeName.isEmpty()) {
            result = result.add(new Landscape(landscapeName, features.toArray(new Feature[features.size()])));
        }
        
        return result;
    }

}
