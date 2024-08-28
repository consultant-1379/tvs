package com.ericsson.gic.tms.tvs.application.comparators;

import com.ericsson.gic.tms.tvs.domain.model.verdict.reports.drop.JobDropReport;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.Comparator;

public class DropNameComparator implements Comparator<JobDropReport>  {

    public int compare(JobDropReport dropReport1, JobDropReport dropReport2) {
        if (dropReport1 == null) {
            if (dropReport2 == null) {
                return 0;
            }
            return -1;
        } else if (dropReport2 == null) {
            return 1;
        }
        DefaultArtifactVersion versionObj1 = new DefaultArtifactVersion(dropReport1.getDropName());
        DefaultArtifactVersion versionObj2 = new DefaultArtifactVersion(dropReport2.getDropName());
        return versionObj1.compareTo(versionObj2);
    }
}
