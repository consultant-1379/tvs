package com.ericsson.gic.tms.tvs.application.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by eniakel on 01/12/2016.
 */
public class VersionConverterTest {

    private static final String VERSION = "1.27.32";
    private static final String VERSION2 = "1.26";

    @Test
    public void testVersionPadding() {
        String paddedVersion = VersionConverter.getPaddedVersion(VERSION);
        assertThat(paddedVersion).isEqualTo("000100270032");

        paddedVersion = VersionConverter.getPaddedVersion(VERSION2);
        assertThat(paddedVersion).isEqualTo("000100260000");

        paddedVersion = VersionConverter.getPaddedVersion(null);
        assertThat(paddedVersion).isEqualTo(null);
    }
}
