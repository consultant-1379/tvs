package com.ericsson.gic.tms.tvs.presentation.converters;

import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseImportStatus;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.ParamConverter;

import static com.google.common.base.Strings.*;

@Component
public class TestCaseImportConverter implements ParamConverter<TestCaseImportStatus> {

    @Override
    public TestCaseImportStatus fromString(String value) {
        if (isNullOrEmpty(value)) {
            return null;
        }
        return TestCaseImportStatus.valueOf(value);
    }

    @Override
    public String toString(TestCaseImportStatus value) {
        if (value == null) {
            return null;
        }
        return value.name();
    }
}
