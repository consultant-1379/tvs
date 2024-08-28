Jenkins job name: ${session.additionalFields.JENKINS_JOB_NAME!"<missing>"}
Drop: ${session.additionalFields.DROP_NAME!"<missing>"}
ISO version: ${session.additionalFields.ISO_VERSION!"<missing>"}

Pass rate: ${session.passRate}%
Started: ${session.time.startDate?datetime}
Finished: ${session.time.stopDate?datetime}
Duration: ${session.time.duration / 1000} sec
Test case count: ${session.testCaseCount}
Test suite count: ${session.testSuiteCount}

URL: ${session.uri}

----------------------------

Rules triggered:

<#list rules as rule>
${rule?counter}: ${rule};
</#list>

----------------------------

This message was sent by Test Reporting Service.
