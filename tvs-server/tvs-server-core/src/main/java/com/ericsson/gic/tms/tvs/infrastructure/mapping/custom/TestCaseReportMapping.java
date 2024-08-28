package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResult;
import com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseReport;
import com.google.common.base.Joiner;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.ericsson.gic.tms.tvs.presentation.constant.FieldNameConst.*;

@Configuration
public class TestCaseReportMapping implements CustomMapping<TestCaseResult, TestCaseReport> {

    @Override
    public ClassMapBuilder<TestCaseResult, TestCaseReport> map(
        ClassMapBuilder<TestCaseResult, TestCaseReport> classMapBuilder) {

        return classMapBuilder
            .field("time.startDate", "startDate")
            .field("time.stopDate", "stopDate")
            .field("time.duration", "duration")
            .exclude("additionalFields");
    }

    @Override
    public void customAtoB(TestCaseResult from, TestCaseReport to) {
        to.setComponents(joinListObject(from.getAdditionalField(COMPONENTS)));
        to.setDropName((String) from.getAdditionalField(DROP_NAME));
        to.setIsoVersion((String) from.getAdditionalField(ISO_VERSION));
        to.setPriority((String) from.getAdditionalField(PRIORITY));
        to.setTitle((String) from.getAdditionalField(TITLE));
        to.setRequirements(joinListObject(from.getAdditionalField(REQUIREMENTS)));
        to.setGroups(joinListObject(from.getAdditionalField(FieldNameConst.GROUPS)));
    }

    private static String joinListObject(Object list) {
        if (list == null) {
            return null;
        } else {
            return Joiner.on(" ").skipNulls().join((List) list);
        }
    }
}
