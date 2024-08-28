package com.ericsson.gic.tms.tvs.application.comparators;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.Comparator;

public class VersionDescendingComparator implements Comparator<String> {

    @Override
    public int compare(String version1, String version2) {
        return new DefaultArtifactVersion(version2).compareTo(new DefaultArtifactVersion(version1));
    }
}
