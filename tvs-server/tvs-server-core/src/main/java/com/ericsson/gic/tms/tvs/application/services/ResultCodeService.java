package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.domain.model.verdict.ResultCode;
import com.ericsson.gic.tms.tvs.domain.repositories.ResultCodeRepository;
import com.ericsson.gic.tms.tvs.presentation.dto.TestExecutionStatus;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.cache.CacheLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map.Entry;
import java.util.Optional;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         13/09/2016
 */

@Service
public class ResultCodeService extends CacheLoader<Entry<String, String>, ResultCode> {

    public static final String DEFAULT_INTERNAL_CODE = TestExecutionStatus.PENDING.name();

    @Autowired
    private ResultCodeRepository resultCodeRepository;

    public ResultCode findByExternalCode(String source, String externalCode) {
        Preconditions.checkNotNull(externalCode);
        ResultCode resultCode;
        if (Strings.isNullOrEmpty(source)) {
            resultCode =  findByExternalCode(externalCode);
        } else {
            resultCode = resultCodeRepository.findBySourceAndExternalCode(source, externalCode);
        }
        return  Optional.ofNullable(resultCode).orElseGet(() -> resolveAsInternal(externalCode));
    }

    public ResultCode findByExternalCode(String externalCode) {
        Preconditions.checkNotNull(externalCode);
        return Optional.ofNullable(resultCodeRepository.findByExternalCode(externalCode))
            .orElseGet(() -> resolveAsInternal(externalCode));
    }

    private static ResultCode resolveAsInternal(String externalCode) {
        ResultCode resultCode = new ResultCode();
        String internalCode;
        try {
            internalCode = TestExecutionStatus.valueOf(externalCode).name();
        } catch (IllegalArgumentException e) {
            internalCode = DEFAULT_INTERNAL_CODE;
            resultCode.setExternalCode(externalCode);
        }
        resultCode.setInternalCode(internalCode);
        return resultCode;
    }

    @Override
    public ResultCode load(Entry<String, String> key) {
        return findByExternalCode(key.getKey(), key.getValue());
    }
}
