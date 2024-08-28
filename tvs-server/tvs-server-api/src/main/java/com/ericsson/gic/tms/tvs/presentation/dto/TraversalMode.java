package com.ericsson.gic.tms.tvs.presentation.dto;

/** Strategies for traversing document tree.
 *
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         15/06/2016
 */
public enum TraversalMode {
    /**
     * Process selected document(s) only.
     */
    SELF,

    /**
     * Traverse all child documents recursively, then process selected document,
     * then process parent document(s).
     */
    WIDE,

    /**
     * Process selected document and then parent document(s).
     */
    ASCENDING,

    /**
     * Traverse all child document(s) recursively and then process selected document.
     */
    DESCENDING
}
