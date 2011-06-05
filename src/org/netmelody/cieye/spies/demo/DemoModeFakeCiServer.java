package org.netmelody.cieye.spies.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class DemoModeFakeCiServer {

    private final String featureName;
    
    private final Map<String, BuildTarget> targets = new HashMap<String, BuildTarget>();

    public DemoModeFakeCiServer(String featureName) {
        this.featureName = featureName;
        
        targets.put(featureName + " - Smoke", new BuildTarget(featureName + " - Smoke"));
        targets.put(featureName + " - Integration", new BuildTarget(featureName + " - Integration"));
        targets.put(featureName + " - Acceptance", new BuildTarget(featureName + " - Acceptance"));
        targets.put(featureName + " - Release", new BuildTarget(featureName + " - Release"));
    }

    public List<String> getTargetNames() {
        final List<String> result = new ArrayList<String>();
        for (BuildTarget target : targets.values()) {
            result.add(target.getName());
        }
        return result;
    }
    
    public void addNote(String targetName, String note) {
        targets.get(targetName).addNote(note);
    }
    
    private static final class BuildTarget {
        private final String targetName;
        private final String url = "http://www.example.com/";
        
        private boolean green;
        private final List<RunningBuild> builds = new ArrayList<RunningBuild>();

        public BuildTarget(String targetName) {
            final Random random = new Random();
            this.targetName = targetName;
            green = random.nextBoolean();
            
            if (random.nextBoolean()) {
                builds.add(new RunningBuild());
            }
        }

        public void addNote(String note) {
            for (RunningBuild build : builds) {
                if (!build.green) {
                    build.addNote(note);
                }
            }
        }

        public String getName() {
            return targetName;
        }
    }
    
    private static final class RunningBuild {
        private final String checkinComments = "dracula: fixed some stuff";
        
        private boolean green = true;
        private int progress;
        private String notes = "";
        
        public RunningBuild() {
            final Random random = new Random();
            progress = random.nextInt(101);
        }

        public void addNote(String note) {
            notes = notes + note + "\n";
        }
        
        public void advanceBy(int percent) {
            
        }
    }
}
