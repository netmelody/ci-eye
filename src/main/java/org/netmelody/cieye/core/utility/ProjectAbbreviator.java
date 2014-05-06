package org.netmelody.cieye.core.utility;

public class ProjectAbbreviator {

    public String abbreviate(String projectName) {
        if (projectName.contains(" ")) {
            StringBuilder out = new StringBuilder();
            for (String s : projectName.split(" ")) {
                out.append(s.charAt(0));
            }
            return out.toString();
        } else {
            return projectName;
        }
    }

}
