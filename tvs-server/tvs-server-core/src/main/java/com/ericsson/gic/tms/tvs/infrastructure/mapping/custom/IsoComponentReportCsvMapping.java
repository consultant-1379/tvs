package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoComponentReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoCsvReport;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IsoComponentReportCsvMapping implements CustomMapping<IsoComponentReportBean, IsoCsvReport> {

    @Override
    public ClassMapBuilder<IsoComponentReportBean, IsoCsvReport> map(
        ClassMapBuilder<IsoComponentReportBean, IsoCsvReport> classMapBuilder) {

        return classMapBuilder
            .field("component", "id");
    }
}
