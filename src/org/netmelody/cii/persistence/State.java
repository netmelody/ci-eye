package org.netmelody.cii.persistence;

import static com.google.common.collect.Lists.newArrayList;
import static org.netmelody.cii.domain.CiServerType.DEMO;
import static org.netmelody.cii.domain.CiServerType.JENKINS;
import static org.netmelody.cii.domain.CiServerType.TEAMCITY;

import java.io.File;

import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.Landscape;
import org.netmelody.cii.domain.LandscapeGroup;

public final class State {

    private LandscapeGroup landscapes =
        new LandscapeGroup(newArrayList(new Landscape("Ci-eye Demo", new Feature("My Product", "", DEMO)),
                                        new Landscape("PubLive", new Feature("Main", "http://hudson.magnolia-cms.com", JENKINS)),
                                        new Landscape("HIP", new Feature("HIP Hawk", "http://ccmain:8080", JENKINS),
                                                             new Feature("HIP - Trunk", "http://teamcity-server:8111", TEAMCITY)),
                                        new Landscape("HIP-HUDSON", new Feature("HIP Hawk", "http://ccmain:8080", JENKINS)),
                                        new Landscape("HIP-TC", new Feature("HIP - Trunk", "http://teamcity-server:8111", TEAMCITY))));

    public State() {
        final String userHome = System.getProperty("user.home");
        
        if (null == userHome || userHome.isEmpty()) {
            throw new IllegalStateException("Unable to get user's home directory.");
        }
        
        final File settingsDir = new File(new File(userHome), ".ci-eye");
        settingsDir.mkdir();
        
        final File viewsFile = new File(settingsDir, "views.txt");
        landscapes = ViewsRepository.readLandscapesFrom(viewsFile);
        
//        final File picturesFile = new File(settingsDir, "pictures.json");
//        try {
//            viewsFile.createNewFile();
//            picturesFile.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        
//        final File picturesDir = new File(settingsDir, "pictures");
//        picturesDir.mkdir();
    }
    
    
    public LandscapeGroup landscapes() {
        return this.landscapes;
    }

    public void addLandscape(Landscape landscape) {
        landscapes = landscapes.add(landscape);
    }

    public Landscape landscapeNamed(String name) {
        return landscapes.landscapeNamed(name);
    }
}
