package com.ericsson.gic.tms.tvs.presentation.utility;

/**
 * Created by eniakel on 26/01/2017.
 */
public enum ChartParameters {
    DATE("Date"),
    DROP("Drop"),
    ISO_VERSION("Iso Version");

    private String chartParameter;

    ChartParameters(String chartParameter) {
        this.chartParameter = chartParameter;
    }

    public String getValue() {
        return chartParameter;
    }

    public static String getChartParamByValue(String chartParameter) {
        String xAxisParameter = "";
        if (chartParameter != null) {
            for (ChartParameters chartParam : ChartParameters.values()) {
                if (chartParam.getValue().equals(chartParameter)) {
                    xAxisParameter = chartParam.getValue();
                }
            }
        }
        return xAxisParameter;
    }
}
