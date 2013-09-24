package org.netmelody.cieye.spies.jenkins;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.netmelody.cieye.core.domain.*;
import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.logging.Logbook;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.jenkins.jsondomain.Job;
import org.netmelody.cieye.spies.jenkins.jsondomain.View;

import com.google.common.base.Predicate;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public final class JenkinsSpy implements CiSpy {
    
    private static final Logbook LOG = LogKeeper.logbookFor(JenkinsSpy.class);
    
    private final JenkinsCommunicator communicator;
    private final JobLaboratory laboratory;
    
    private final Map<TargetId, Job> recognisedJobs = newHashMap();
    
    public JenkinsSpy(String endpoint, KnownOffendersDirectory detective, Contact contact) {
        this.communicator = new JenkinsCommunicator(endpoint, contact);
        this.laboratory = new JobLaboratory(communicator, detective);
    }

    @Override
    public TargetDigestGroup targetsConstituting(Feature feature) {
        final Collection<Job> jobs = jobsFor(feature);
        final List<TargetDigest> digests = newArrayList();
        
        for (Job job : jobs) {
            final TargetDigest targetDigest = new TargetDigest(job.url, job.url, job.name, job.status());
            digests.add(targetDigest);
            recognisedJobs.put(targetDigest.id(), job);
        }
        
        return new TargetDigestGroup(digests);
    }

    @Override
    public TargetDetail statusOf(final TargetId target, Flag showPersonalBuilds) {
        Job job = recognisedJobs.get(target);
        if (null == job) {
            return null;
        }
        return laboratory.analyseJob(job);
    }

    @Override
    public boolean takeNoteOf(TargetId target, String note) {
        if (!recognisedJobs.containsKey(target)) {
            return false;
        }
        
        final Job job = this.recognisedJobs.get(target);
        final String buildUrl = this.laboratory.lastBadBuildUrlFor(job);
        
        if (buildUrl.isEmpty()) {
            return true;
        }
        
        communicator.doJenkinsPost(buildUrl +
                                   "submitDescription?" +
                                   URLEncodedUtils.format(newArrayList(new BasicNameValuePair("description", note)), "UTF-8"));
        return true;
    }
    
    private Collection<Job> jobsFor(final Feature feature) {
        if (!communicator.canSpeakFor(feature)) {
            return newArrayList();
        }
        
        final String name = feature.name().isEmpty() ? "All" : feature.name();
        final View viewDigest = find(communicator.views(), withName(name), null);
        if (null == viewDigest) {
            LOG.error("No view named <" + name + "> found");
            return newArrayList();
        }
        
        return communicator.jobsFor(viewDigest);
    }

    private Predicate<View> withName(final String featureName) {
        return new Predicate<View>() {
            @Override public boolean apply(View viewDigest) {
                return viewDigest.name.trim().equals(featureName.trim());
            }
        };
    }
}
