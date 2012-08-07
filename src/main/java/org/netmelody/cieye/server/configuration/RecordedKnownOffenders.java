package org.netmelody.cieye.server.configuration;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.netmelody.cieye.core.domain.Sponsor;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.netmelody.cieye.server.configuration.avatar.PictureUrlRegistry;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.skip;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;
import static org.netmelody.cieye.core.utility.Irritables.partition;

public final class RecordedKnownOffenders implements KnownOffendersDirectory, Refreshable {
    
    private static final Pattern PICTURE_FILENAME_REGEX = Pattern.compile("^\\s*\\[(.*)\\]\\s*$");

    private static final PictureUrlRegistry pictureUrlRegistry = new PictureUrlRegistry();
    
    private final SettingsFile picturesFile;
    
    private Iterable<Biometric> biometrics = newArrayList();
    
    public RecordedKnownOffenders(SettingsFile picturesFile) {
        this.picturesFile = picturesFile.newReference();
        loadPictureSettings();
    }

    @Override
    public void refresh() {
        if (picturesFile.updateAvailable()) {
            loadPictureSettings();
        }
    }
    
    @Override
    public Set<Sponsor> search(String crimeScene) {
        return unmodifiableSet(newHashSet(transform(filter(biometrics, foundAt(crimeScene)), toSponsor())));
    }
    
    private void loadPictureSettings() {
        biometrics = extractPicuresFrom(picturesFile.readContent());
    }

    private static Iterable<Biometric> extractPicuresFrom(List<String> content) {
        return transform(skip(partition(content, byPicture()), 1), toBiometric());
    }

    private Function<Biometric, Sponsor> toSponsor() {
        return new Function<Biometric, Sponsor>() {
            @Override public Sponsor apply(Biometric bio) {
                return bio.sponsor();
            }
        };
    }

    private Predicate<Biometric> foundAt(final String crimescene) {
        return new Predicate<Biometric>() {
            @Override public boolean apply(Biometric bio) {
                return bio.foundAt(crimescene);
            }
        };
    }
    
    private static Predicate<String> byPicture() {
        return new Predicate<String>() {
            @Override public boolean apply(String line) {
                return PICTURE_FILENAME_REGEX.matcher(line).matches();
            }
        };
    }
    
    private static Function<List<String>, Biometric> toBiometric() {
        return new Function<List<String>, Biometric>() {
            @Override public Biometric apply(List<String> data) {
                final Matcher matcher = PICTURE_FILENAME_REGEX.matcher(data.get(0));
                if (!matcher.matches()) {
                    throw new IllegalStateException();
                }
                
                final String pictureUrl = getPictureUrl(matcher.group(1));
                final Iterable<String> fingerprints = filter(skip(data, 1), notBlank());
                final String name = getFirst(fingerprints, pictureUrl);
                return new Biometric(new Sponsor(name, pictureUrl), fingerprints);
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

    private static String getPictureUrl(final String image) {
      return pictureUrlRegistry.getPictureUrl(image);
    }
}
