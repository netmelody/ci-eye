package org.netmelody.cieye.server.configuration;

import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.netmelody.cieye.core.domain.Sponsor;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public final class Biometric {
    private static final Pattern FINGERPRINT_TEMPLATE = Pattern.compile(".*?\\b(.*)\\b.*?");
    
    private final Sponsor sponsor;
    private final Iterable<Pattern> fingerprints;
    
    public Biometric(Sponsor sponsor, Iterable<String> fingerprints) {
        this.sponsor = sponsor;
        this.fingerprints = transform(cleaned(fingerprints), toPatterns());
    }
    
    public Sponsor sponsor() {
        return sponsor;
    }
    
    public boolean foundAt(final String crimescene) {
        return any(fingerprints, leftAt(crimescene));
    }
    
    private Iterable<String> cleaned(Iterable<String> dirtyFingerprints) {
        final Iterable<Matcher> impressions = transform(dirtyFingerprints, toFingerprintImpressions());
        return transform(filter(impressions, smudges()), toFingerprints());
    }

    private static Function<String, Matcher> toFingerprintImpressions() {
        return new Function<String, Matcher>() {
            @Override public Matcher apply(String dirtyPrint) { return FINGERPRINT_TEMPLATE.matcher(dirtyPrint); }
        };
    }

    private static Predicate<Matcher> smudges() {
        return new Predicate<Matcher>() {
            @Override public boolean apply(Matcher matcher) { return matcher.matches(); }
        };
    }

    private static Function<Matcher, String> toFingerprints() {
        return new Function<Matcher, String>() {
            @Override public String apply(Matcher matcher) { return matcher.group(1); }
        };
    }

    private static Predicate<Pattern> leftAt(final String crimescene) {
        return new Predicate<Pattern>() {
            @Override public boolean apply(Pattern fingerprint) {
                return fingerprint.matcher(crimescene).find();
            }
        };
    }
    
    private static Function<String, Pattern> toPatterns() {
        return new Function<String, Pattern>() {
            @Override public Pattern apply(String keyword) {
                return Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b", Pattern.CASE_INSENSITIVE);
            }
        };
    }
}