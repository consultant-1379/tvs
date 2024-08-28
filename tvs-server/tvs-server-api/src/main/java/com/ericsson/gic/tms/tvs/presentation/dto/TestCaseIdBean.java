package com.ericsson.gic.tms.tvs.presentation.dto;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestCaseIdBean implements Attributes<String> {

    private String systemId;

    @Override
    public String getId() {
        return systemId;
    }

    public void setId(String systemId) {
        this.systemId = systemId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

}
