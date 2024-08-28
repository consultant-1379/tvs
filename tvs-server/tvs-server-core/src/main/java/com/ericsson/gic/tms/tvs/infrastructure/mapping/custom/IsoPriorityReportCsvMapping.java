package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoCsvReport;
import com.ericsson.gic.tms.tvs.presentation.dto.reports.IsoPriorityReportBean;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IsoPriorityReportCsvMapping implements CustomMapping<IsoPriorityReportBean, IsoCsvReport> {

    @Override
    public ClassMapBuilder<IsoPriorityReportBean, IsoCsvReport> map(
        ClassMapBuilder<IsoPriorityReportBean, IsoCsvReport> classMapBuilder) {

        return classMapBuilder
            .field("priority", "id");
    }
}
