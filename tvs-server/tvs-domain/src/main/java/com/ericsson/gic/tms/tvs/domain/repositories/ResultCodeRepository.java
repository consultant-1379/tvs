package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.domain.model.TvsCollections;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ResultCode;
import org.springframework.stereotype.Repository;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         13/09/2016
 */
@Repository
public class ResultCodeRepository extends BaseJongoRepository<ResultCode, String> {

    public ResultCodeRepository() {
        super(ResultCode.class);
    }

    @Override
    protected String getCollectionName() {
        return TvsCollections.RESULT_CODE.getName();
    }

    public ResultCode findByExternalCode(String externalCode) {
        return findOneBy("{externalCode: #}", externalCode);
    }

    public ResultCode findBySourceAndExternalCode(String source, String externalCode) {
        return findOneBy("{source: #, externalCode: #}", source, externalCode);
    }
}
