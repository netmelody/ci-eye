package org.netmelody.cieye.server.configuration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.netmelody.cieye.core.domain.Sponsor;
import org.netmelody.cieye.server.configuration.RecordedKnownOffenders;
import org.netmelody.cieye.server.configuration.SettingsFile;
import org.netmelody.cieye.server.configuration.avatar.Gravatar;


public final class RecordedKnownOffendersTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    private RecordedKnownOffenders offenders;

    @Before
    public void createOffendersFile() throws IOException {
        final File peeps = testFolder.newFile("peeps.txt");
        FileUtils.copyInputStreamToFile(RecordedKnownOffendersTest.class.getResourceAsStream("testPictures.txt"), peeps);
        offenders = new RecordedKnownOffenders(new SettingsFile(peeps));
    }
    
    @Test public void
    looksUpSimpleOffenderStrings() {
        assertThat(offenders.search("vlad"), contains(new Sponsor("", "/pictures/vlad.png")));
    }
    
    @Test public void
    looksUpSimpleOffenderStringsWithAlias() {
        assertThat(offenders.search("dracula"), contains(new Sponsor("", "/pictures/vlad.png")));
    }
    
    @Test public void
    looksUpSimpleOffenderStringsCaseInsensitively() {
        assertThat(offenders.search("VlAd"), contains(new Sponsor("", "/pictures/vlad.png")));
    }
    
    @Test public void
    looksUpSpacedOffenderStrings() {
        assertThat(offenders.search(" vlad "), contains(new Sponsor("", "/pictures/vlad.png")));
    }
    
    @Test public void
    looksUpWrappedOffenderStrings() {
        assertThat(offenders.search("-vlad-"), contains(new Sponsor("", "/pictures/vlad.png")));
    }
    
    @Test public void
    looksUpOffenderStringsAtTheBegginingOfALine() {
        assertThat(offenders.search("someguff\nvlad: did stuff"), contains(new Sponsor("", "/pictures/vlad.png")));
    }
    
    @Test public void
    looksUpOffendersWhoseFingerprintsAreWrappedInHyphens() {
        assertThat(offenders.search("doh"), contains(new Sponsor("", "/pictures/doh.png")));
    }
    
    @Test public void
    looksUpMultipleOffenders() {
        assertThat(offenders.search("vlad/stupid:"), contains(new Sponsor("", "/pictures/vlad.png"),
                                                              new Sponsor("", "/pictures/stupid.png")));
    }
    
    @Test public void
    ignoresOffenderNamesAppearingInTheMiddleOfAWord() {
        assertThat(offenders.search("markoVLADies"), is(Matchers.<Sponsor>empty()));
    }
    
    @Test public void
    looksUpOffenderAndProvidesLinkToGravatarMugshot() {
        final String gravatarUrlWithHashedEmail = new Gravatar().imageUrlFor("john.doe@gmail.com");
        assertThat(offenders.search("john"), contains(new Sponsor("", gravatarUrlWithHashedEmail)));
    }
}
