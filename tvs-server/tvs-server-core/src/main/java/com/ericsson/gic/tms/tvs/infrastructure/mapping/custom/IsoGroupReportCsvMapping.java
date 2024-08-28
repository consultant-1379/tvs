package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoGroupReportBean;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoCsvReport;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IsoGroupReportCsvMapping implements CustomMapping<IsoGroupReportBean, IsoCsvReport> {

    @Override
    public ClassMapBuilder<IsoGroupReportBean, IsoCsvReport> map(
        ClassMapBuilder<IsoGroupReportBean, IsoCsvReport> classMapBuilder) {

        return classMapBuilder
            .field("group", "id");
    }
}
