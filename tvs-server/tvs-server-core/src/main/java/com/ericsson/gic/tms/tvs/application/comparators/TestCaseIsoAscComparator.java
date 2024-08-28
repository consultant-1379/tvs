package com.ericsson.gic.tms.tvs.application.comparators;

import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.TestCaseResultTrendReport;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.Comparator;

public class TestCaseIsoAscComparator implements Comparator<TestCaseResultTrendReport> {

    public int compare(TestCaseResultTrendReport isoTrend1, TestCaseResultTrendReport isoTrend2) {
        if (isoTrend1 == null) {
            if (isoTrend2 == null) {
                return 0;
            }
            return -1;
        } else if (isoTrend2 == null) {
            return 1;
        }
        DefaultArtifactVersion versionObj1 = new DefaultArtifactVersion(isoTrend1.getIsoVersion());
        DefaultArtifactVersion versionObj2 = new DefaultArtifactVersion(isoTrend2.getIsoVersion());
        return versionObj1.compareTo(versionObj2);
    }
}
