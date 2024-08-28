package com.ericsson.gic.tms.tvs.infrastructure.changelog;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.ericsson.gic.tms.tvs.infrastructure.changelog.ChangeSetOrder.*;

@ChangeLog
public class IndexesChangelog {

    @ChangeSet(order = ORDER_001, id = "initialIndexes", author = "eserish")
    public void indexes(Jongo jongo) {
        MongoCollection job = jongo.getCollection(JOB.getName());
        job.ensureIndex("{'name' : 1, 'contextId' : 1}", "{name: 'name_context', unique: 1}");
        job.ensureIndex("{'uid' : 1}", "{unique: 1}");
        job.ensureIndex("{'contextId' : 1}");

        MongoCollection session = jongo.getCollection(TEST_SESSION.getName());
        session.ensureIndex("{'jobId': 1}");
        session.ensureIndex("{'executionId': 1}");
        session.ensureIndex("{'time.startDate': 1}");
        session.ensureIndex("{'time.stopDate': 1}");

        MongoCollection suite = jongo.getCollection(TEST_SUITE_RESULT.getName());
        suite.ensureIndex("{'testSessionId': 1}");
        suite.ensureIndex("{'name': 1}");
        suite.ensureIndex("{'time.startDate': 1}");
        suite.ensureIndex("{'time.stopDate': 1}");

        MongoCollection testCase = jongo.getCollection(TEST_CASE_RESULT.getName());
        testCase.ensureIndex("{'contextId': 1}");
        testCase.ensureIndex("{'jobId': 1}");
        testCase.ensureIndex("{'executionId': 1}");
        testCase.ensureIndex("{'testSuiteName': 1}");
        testCase.ensureIndex("{'testSuiteResultId': 1}");
        testCase.ensureIndex("{'testCaseId': 1}");
        testCase.ensureIndex("{'name': 1}");
        testCase.ensureIndex("{'time.startDate': 1}");
        testCase.ensureIndex("{'time.stopDate': 1}");
    }

    @ChangeSet(order = ORDER_003, id = "collectionMetadataIndexes", author = "enaiand")
    public void collectionMetadataIndexes(Jongo jongo) {
        MongoCollection collectionMetadata = jongo.getCollection(COLLECTION_METADATA.getName());
        collectionMetadata.ensureIndex("{'name': 1}", "{unique: 1}");
    }

    @ChangeSet(order = ORDER_012, id = "projectRequirementIndex", author = "erusbob")
    public void projectRequirementIndex(Jongo jongo) {
        MongoCollection collectionMetadata = jongo.getCollection(PROJECT_REQUIREMENT.getName());
        collectionMetadata.ensureIndex("{'_id': 1}", "{unique: 1}");
        collectionMetadata.ensureIndex("{'type': 1}");
    }

    @ChangeSet(order = ORDER_014, id = "allureReportJobIndex", author = "erusbob")
    public void allureReportJobIndex(Jongo jongo) {
        MongoCollection collectionMetadata = jongo.getCollection(ALLURE_REPORT_LOG.getName());
        collectionMetadata.ensureIndex("{'_id': 1}", "{unique: 1}");
        collectionMetadata.ensureIndex("{'jobExecutionId': 1}", "{unique: 1}");
    }
}
