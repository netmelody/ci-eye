package org.netmelody.cieye.server.configuration;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.LandscapeGroup;

public final class ViewsRepository {

    private static final Log LOG = LogFactory.getLog(ViewsRepository.class);
    private static final Pattern LANDSCAPE_NAME_REGEX = Pattern.compile("^\\s*\\[(.*)\\]\\s*$");
    private static final Pattern FEATURE_REGEX = Pattern.compile("^(.*?)\\|(.*?)\\|(.*?)$");

    private final File viewsFile;
    
    public ViewsRepository(File viewsFile) {
        this.viewsFile = viewsFile;
    }
    
    public LandscapeGroup landscapes() {
        if (!viewsFile.canRead()) {
            return new LandscapeGroup(newArrayList(new Landscape("Ci-eye Demo", new Feature("My Product", "", new CiServerType("DEMO")))));
        }
        
        try {
            final List<String> content = FileUtils.readLines(viewsFile);
            return extractLandscapeFrom(content);
        } catch (IOException e) {
            LOG.error("failed to read view settings file", e);
        }
        
        return new LandscapeGroup(newArrayList(new Landscape("Ci-eye Demo", new Feature("My Product", "", new CiServerType("DEMO")))));
    }

    private static LandscapeGroup extractLandscapeFrom(List<String> content) {
        LandscapeGroup result = new LandscapeGroup();
        
        String landscapeName = "";
        List<Feature> features = new ArrayList<Feature>();
        
        for (String line : content) {
            Matcher landscapeMatcher = LANDSCAPE_NAME_REGEX.matcher(line);
            if (landscapeMatcher.matches()) {
                if (landscapeName.length() > 0) {
                    result = result.add(new Landscape(landscapeName, features.toArray(new Feature[features.size()])));
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
            result = result.add(new Landscape(landscapeName, features.toArray(new Feature[features.size()])));
        }
        
        return result;
    }

}
