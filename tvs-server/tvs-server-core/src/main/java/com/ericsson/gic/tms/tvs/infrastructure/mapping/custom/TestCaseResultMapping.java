package com.ericsson.gic.tms.tvs.infrastructure.mapping.custom;

import com.ericsson.gic.tms.infrastructure.mapping.mappers.CustomMapping;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ResultCode;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestCaseResult;
import com.ericsson.gic.tms.tvs.presentation.dto.TestCaseResultBean;
import com.google.common.base.Throwables;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import static com.google.common.base.MoreObjects.firstNonNull;

@Configuration
public class TestCaseResultMapping implements CustomMapping<TestCaseResult, TestCaseResultBean> {

    @Autowired
    private LoadingCache<Entry<String, String>, ResultCode> resultCodeCache;

    @Override
    public ClassMapBuilder<TestCaseResult, TestCaseResultBean> map(
        ClassMapBuilder<TestCaseResult, TestCaseResultBean> classMapBuilder) {

        return classMapBuilder
            .field("testCaseId", "id")
            .fieldAToB("createdDate", "createdDate")
            .exclude("additionalFields")
            .exclude("resultCode");
    }

    /**
     * Due to a strange Orika behaviour in case of handling collection as an additional field value data can be lost
     */
    @Override
    public void customBtoA(TestCaseResultBean from, TestCaseResult to) {
        to.setAdditionalFields(from.getAdditionalFields());
        mapResultCode(from, to);
    }

    @Override
    public void customAtoB(TestCaseResult from, TestCaseResultBean to) {
        to.setAdditionalFields(from.getAdditionalFields());
        to.setResultCode(from.getResultCode());
    }

    private void mapResultCode(TestCaseResultBean dto, TestCaseResult entity) {
        String externalCode = firstNonNull(dto.getExternalResultCode(), dto.getResultCode());
        SimpleEntry<String, String> key = new SimpleEntry<>(dto.getSource(), externalCode);
        String internalCode = null;
        try {
            internalCode = resultCodeCache.getUnchecked(key).getInternalCode();
        } catch (UncheckedExecutionException e) {
            Throwables.propagate(e.getCause());
        }
        if (!internalCode.equals(entity.getResultCode())) {
            entity.setResultCode(internalCode);
            if (!externalCode.equals(internalCode)) {
                entity.setExternalResultCode(externalCode);
            }
        }
    }
}
