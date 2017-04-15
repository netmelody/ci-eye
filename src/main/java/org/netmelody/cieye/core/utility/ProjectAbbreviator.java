package org.netmelody.cieye.core.utility;

import java.util.regex.Pattern;

public class ProjectAbbreviator {

    public String abbreviate(String projectName) {
        StringBuilder out = new StringBuilder();
        String[] nameParts = Pattern.compile("[-\\s]+").split(projectName);
        if (nameParts.length == 1) {
            return projectName;
        } else {
            for (String s : nameParts) {
                out.append(s.charAt(0));
            }
            return out.toString();
        }
    }


    public static String nameWithProjectAbbreviation(String projectName, String buildName) {
        return new ProjectAbbreviator().abbreviate(projectName) + ": " + buildName;
    }
}