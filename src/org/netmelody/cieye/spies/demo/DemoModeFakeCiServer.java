package org.netmelody.cieye.spies.demo;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class DemoModeFakeCiServer {

    private final Map<String, BuildTarget> targets = new HashMap<String, BuildTarget>();

    public DemoModeFakeCiServer(String featureName) {
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
        final BuildTarget buildTarget = targets.get(targetName);
        if (null != buildTarget) {
            buildTarget.addNote(note);
        }
    }
    
    public TargetData getDataFor(String targetName) {
        final BuildTarget buildTarget = targets.get(targetName);
        synchronized(buildTarget) {
            buildTarget.recalculate();
            final List<BuildData> buildData = new ArrayList<BuildData>();
            for (ActiveBuild build : buildTarget.builds) {
                buildData.add(new BuildData(build.progress, build.green, "dracula"));
            }
            return new TargetData(buildTarget.url, buildTarget.green, buildTarget.note, buildData);
        }
    }

    public static final class TargetData {
        public final String url;
        public final boolean green;
        public final List<BuildData> builds;
        public final String note;
        public TargetData(String url, boolean green, String note, List<BuildData> buildData) {
            this.url = url;
            this.green = green;
            this.note = note;
            this.builds = unmodifiableList(new ArrayList<BuildData>(buildData));
        }
    }
    
    public static final class BuildData {
        public final int progress;
        public final boolean green;
        public final String checkinComments;
        public BuildData(int progress, boolean green, String comment) {
            this.progress = progress;
            this.green = green;
            this.checkinComments = comment;
        }
    }
    
    private static final class BuildTarget {
        private final Random random = new Random();
        private final String targetName;
        private final String url;
        
        private boolean green;
        private String note = "";
        private final List<ActiveBuild> builds = new ArrayList<ActiveBuild>();
        private long lastUpdateTime = System.currentTimeMillis();

        public BuildTarget(String targetName) {
            this.targetName = targetName;
            this.url = "http://www.example.com/" + targetName;
            green = random.nextInt(5) != 0;
            
            if (random.nextInt(5) == 0) {
                builds.add(new ActiveBuild());
            }
        }

        public void recalculate() {
            final long currentTime = System.currentTimeMillis();
            final long ticksPassed = (currentTime - lastUpdateTime) / 1000;
            advanceBy((int)ticksPassed);
            this.lastUpdateTime  = currentTime;
        }

        public void addNote(String note) {
            if (!green) {
                this.note = note;
            }
        }

        public String getName() {
            return targetName;
        }
        
        public void advanceBy(int percent) {
            Iterator<ActiveBuild> buildIterator = builds.iterator();
            while (buildIterator.hasNext()) {
                ActiveBuild build = buildIterator.next();
                build.advanceBy(percent);
                if (build.complete()) {
                    buildIterator.remove();
                    green = build.green;
                    note = "";
                }
            }
            
            if (builds.isEmpty()) {
                if (random.nextInt(green ? 30 : 10) == 0) {
                    builds.add(new ActiveBuild(2));
                }
            }
        }
    }
    
    private static final class ActiveBuild {
        private final Random random = new Random();
        private boolean green = true;
        private int progress;
        
        public ActiveBuild() {
            progress = random.nextInt(101);
        }

        public synchronized boolean complete() {
            return progress >= 100;
        }

        public ActiveBuild(int initialProgress) {
            progress = initialProgress;
        }

        public void advanceBy(int percent) {
            int prob = 0;
            
            synchronized (this) {
                final int oldProgress = progress;
                progress = Math.min(100, progress + percent);
                
                if (oldProgress > 90 || progress < 60) {
                    return;
                }
                
                prob = Math.max(Math.min(progress, 90) - 60, 0) - Math.min(oldProgress - 60, 0);
            }
            
            if (random.nextInt(100) < prob) {
                green = false;
            }
        }
    }
}
