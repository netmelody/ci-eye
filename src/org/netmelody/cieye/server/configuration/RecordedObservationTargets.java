package org.netmelody.cieye.server.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.LandscapeGroup;
import org.netmelody.cieye.server.LandscapeFetcher;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.skip;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.netmelody.cieye.core.utility.Irritables.partition;

public final class RecordedObservationTargets implements LandscapeFetcher, Refreshable {

    private static final Pattern LANDSCAPE_NAME_REGEX = Pattern.compile("^\\s*\\[(.*)\\]\\s*$");
    private static final Pattern FEATURE_REGEX = Pattern.compile("^(.*?)\\|(.*?)\\|(.*?)$");

    private final SettingsFile viewsFile;
    
    private LandscapeGroup landscapeGroup;
    
    public RecordedObservationTargets(SettingsFile viewsFile) {
        this.viewsFile = viewsFile;
        loadFromFile();
    }

    @Override
    public void refresh() {
        if (viewsFile.updateAvailable()) {
            loadFromFile();
        }
    }

    private void loadFromFile() {
        final List<Landscape> landscapes = extractLandscapesFrom(viewsFile.readContent());
        
        if (landscapes.isEmpty()) {
            this.landscapeGroup = new LandscapeGroup(newArrayList(new Landscape("Ci-eye Demo", new Feature("My Product", "", new CiServerType("DEMO")))));
            return;
        }
        
        this.landscapeGroup = new LandscapeGroup(landscapes);
    }
    
    public LandscapeGroup landscapes() {
        return this.landscapeGroup;
    }
    
    @Override
    public Landscape landscapeNamed(String name) {
        return this.landscapeGroup.landscapeNamed(name);
    }
    
    private static Predicate<String> byLandscape() {
        return new Predicate<String>() {
            @Override public boolean apply(String line) {
                return LANDSCAPE_NAME_REGEX.matcher(line).matches();
            }
        };
    }
    
    private static List<Landscape> extractLandscapesFrom(List<String> content) {
        return newArrayList(transform(skip(partition(content, byLandscape()), 1), toLandscape()));
    }

    private static Function<List<String>, Landscape> toLandscape() {
        return new Function<List<String>, Landscape>() {
            @Override public Landscape apply(List<String> data) {
                final Matcher matcher = LANDSCAPE_NAME_REGEX.matcher(data.get(0));
                if (!matcher.matches()) {
                    throw new IllegalStateException();
                }
                
                final String landscapeName = matcher.group(1);
                final Iterable<String> featuresNames = filter(skip(data, 1), notBlank());
                
                final List<Feature> features = new ArrayList<Feature>();
                for (String featurename : featuresNames) {
                    Matcher featureMatcher = FEATURE_REGEX.matcher(featurename);
                    if (featureMatcher.matches()) {
                        features.add(new Feature(featureMatcher.group(3),
                                                 featureMatcher.group(2),
                                                 new CiServerType(featureMatcher.group(1))));
                    }
                }
                return new Landscape(landscapeName, features.toArray(new Feature[features.size()]));
            }
        };
    }
    
    private static Predicate<String> notBlank() {
        return new Predicate<String>() {
            @Override public boolean apply(String line) {
                return line.trim().length() > 0;
            }
        };
    }
}
