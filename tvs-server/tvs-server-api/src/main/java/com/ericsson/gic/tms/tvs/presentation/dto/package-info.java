/*
 * COPYRIGHT Ericsson (c) 2015.
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

/**
 * <p>Welcome to the API for Global TMS system! Feel free to check it out.</p>
 *
 * <h1>Disclaimer</h1>
 *
 * <p>You are free to use API as long as you agree to the
 * terms of service which dictate that you give up your right to privacy.</p>
 *
 * <h1>JSON API</h1>
 * <p>Global TMS is <a href="http://jsonapi.org/">JSON API</a> compliant (Except for LoginResource).</p>
 * <p>Every response is wrapped in a JSON object with at least one of three members: data, errors or meta.
 * This fact is not reflected in Response Body sections of this documentation.</p>
 */
@XmlSchema(
    xmlns = {
        @XmlNs(prefix = "tvs-api", namespaceURI = "http://www.ericsson.com/tvs-api")
    },
    namespace = "http://www.ericsson.com/tvs-api",
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package com.ericsson.gic.tms.tvs.presentation.dto;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
