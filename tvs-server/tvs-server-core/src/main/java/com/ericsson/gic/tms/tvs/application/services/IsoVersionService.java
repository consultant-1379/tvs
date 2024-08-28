package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.application.comparators.VersionDescendingComparator;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static org.springframework.util.CollectionUtils.*;

@Service
public class IsoVersionService {

    @Autowired
    private TestSessionService testSessionService;

    public List<String> findLastIsoVersionsTopN(List<String> contextIds, long maxSize) {
        checkArgument(!isEmpty(contextIds), "At least 1 context must be specified");

        List<String> contextIsoVersions = testSessionService.getAllIsoVersions(contextIds).stream()
            .limit(maxSize)
            .collect(toList());
        sort(contextIsoVersions, new VersionDescendingComparator());
        return contextIsoVersions;
    }

    public List<String> findIsoVersionsInContexts(List<String> contextIds, String fromIsoVersion, String toIsoVersion) {
        checkArgument(!isEmpty(contextIds), "At least 1 context must be specified");

        List<String> contextIsoVersions = testSessionService.getAllIsoVersions(contextIds);

        List<String> filtered = contextIsoVersions.stream()
            .filter(version ->
                isMoreThan(fromIsoVersion, version) && isLessThan(toIsoVersion, version))
            .collect(toList());
        return filtered;
    }

    private static boolean isLessThan(String versionLessThan, String version) {
        if (isNullOrEmpty(versionLessThan)) {
            return true;
        }
        return new DefaultArtifactVersion(versionLessThan).compareTo(new DefaultArtifactVersion(version)) >= 0;
    }

    private static boolean isMoreThan(String versionMoreThan, String version) {
        if (isNullOrEmpty(versionMoreThan)) {
            return true;
        }
        return new DefaultArtifactVersion(versionMoreThan).compareTo(new DefaultArtifactVersion(version)) <= 0;
    }
}
