package com.ericsson.gic.tms.tvs.presentation.dto;

public enum RequirementType {

    /**
     * A top hierarchy of requirements
     */
    PROJECT,

    /**
     * Main requirement
     */
    MR,

    /**
     * Requirement of EPIC type. May contain test cases
     */
    EPIC,

    /**
     * User story type. May contain test cases
     */
    STORY
}
