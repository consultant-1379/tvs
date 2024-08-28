package com.ericsson.gic.tms.tvs.application.utility;

import com.ericsson.gic.tms.tvs.presentation.utility.ChartParameters;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by eniakel on 30/01/2017.
 */
public class ChartParametersTest {

    private static final String DATE = "Date";
    private static final String ISO_VERSION = "Iso Version";
    private static final String DROP = "Drop";

    @Test
    public void getChartParameter() {
        String chartParam = "";
        chartParam = ChartParameters.getChartParamByValue(DATE);
        assertThat(chartParam).isEqualTo(DATE);

        chartParam = ChartParameters.getChartParamByValue(ISO_VERSION);
        assertThat(chartParam).isEqualTo(ISO_VERSION);

        chartParam = ChartParameters.getChartParamByValue(DROP);
        assertThat(chartParam).isEqualTo(DROP);
    }
}
