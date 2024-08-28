package com.ericsson.gic.tms.tvs.application.util;

import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by eniakel on 15/11/2016.
 */
public final class VersionConverter {

    private static final String DEFAULT_VERSION = "0000";

    private VersionConverter() {
        // private constructor for utility class
    }

    public static String getPaddedVersion(String isoVersion) {
        String paddedVersion = null;

        if (isoVersion != null) {
            List<String> versionComponents = Arrays.asList(isoVersion.split(Pattern.quote(".")));

            String major = DEFAULT_VERSION;
            if (versionComponents.size() > 0) {
                major = Strings.padStart(versionComponents.get(0), 4, '0');
            }

            String minor = DEFAULT_VERSION;
            if (versionComponents.size() > 1) {
                minor = Strings.padStart(versionComponents.get(1), 4, '0');
            }

            String revision = DEFAULT_VERSION;
            if (versionComponents.size() > 2) {
                revision = Strings.padStart(versionComponents.get(2), 4, '0');
            }

            paddedVersion = major + minor + revision;
        }
        return paddedVersion;
    }
}
