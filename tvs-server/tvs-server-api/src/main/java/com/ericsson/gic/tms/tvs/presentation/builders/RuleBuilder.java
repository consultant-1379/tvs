package com.ericsson.gic.tms.tvs.presentation.builders;

import com.ericsson.gic.tms.tvs.presentation.dto.RuleBean;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         18/04/2017
 */
public final class RuleBuilder {
    private RuleBean bean = new RuleBean();

    public RuleBuilder() {
        //needed for parsing
    }

    public RuleBuilder withId(String id) {
        bean.setId(id);
        return this;
    }

    public RuleBuilder withField(String field) {
        bean.setField(field);
        return this;
    }

    public RuleBuilder withFieldType(String fieldType) {
        bean.setFieldType(fieldType);
        return this;
    }

    public RuleBuilder withOperation(String operation) {
        bean.setOperation(operation);
        return this;
    }

    public RuleBuilder withValue(Object value) {
        bean.setValue(value);
        return this;
    }

    public RuleBean build() {
        return bean;
    }
}
