package com.ericsson.gic.tms.tvs.presentation.dto;

import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

import static com.ericsson.gic.tms.tvs.presentation.dto.TestCaseImportStatus.PENDING;

@XmlRootElement
public class TestCaseResultBean extends AdditionalFieldAware implements Attributes<String>, HasTime, ResetFields {

    private String id;

    /**
     * Named Id of Test Case
     */
    @NotNull
    private String name;

    @Valid
    @NotNull
    private ExecutionTimeBean time;

    @NotNull
    private String resultCode;

    private String externalResultCode;

    private String source;

    /**
     * By default in pending status
     */
    private TestCaseImportStatus importStatus = PENDING;

    @Null
    private Date modifiedDate;

    @Null
    private Date createdDate;

    /**
     * @return surrogate key
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * <p>Sets a surrogate key</p>
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Named ID of Test Case
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Sets Named ID of Test Case</p>
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ExecutionTimeBean getTime() {
        return time;
    }

    public void setTime(ExecutionTimeBean time) {
        this.time = time;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getExternalResultCode() {
        return externalResultCode;
    }

    public void setExternalResultCode(String externalResultCode) {
        this.externalResultCode = externalResultCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public TestCaseImportStatus getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(TestCaseImportStatus importStatus) {
        this.importStatus = importStatus;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public void resetImmutableFields() {
        this.modifiedDate = null;
        this.createdDate = null;
        this.time.resetImmutableFields();
    }
}
