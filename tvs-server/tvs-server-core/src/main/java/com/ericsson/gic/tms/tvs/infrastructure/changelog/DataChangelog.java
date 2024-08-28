package com.ericsson.gic.tms.tvs.infrastructure.changelog;

import com.ericsson.gic.tms.tvs.application.util.VersionConverter;
import com.ericsson.gic.tms.tvs.domain.model.verdict.CollectionMetadata;
import com.ericsson.gic.tms.tvs.domain.model.verdict.FieldMetadata;
import com.ericsson.gic.tms.tvs.domain.model.verdict.FieldType;
import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.RawResultHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.ericsson.gic.tms.tvs.domain.model.verdict.FieldType.*;
import static com.ericsson.gic.tms.tvs.infrastructure.changelog.ChangeSetOrder.*;
import static com.ericsson.gic.tms.tvs.presentation.constant.TestCaseResultSource.*;
import static com.ericsson.gic.tms.tvs.presentation.dto.TestExecutionStatus.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.io.Resources.*;
import static java.nio.charset.StandardCharsets.*;

@ChangeLog
public class DataChangelog {

    @ChangeSet(order = ORDER_002, id = "collectionMetadata", author = "enaiand")
    public void metadata(Jongo jongo) {
        // Deprecated due to domain changes
    }

    @ChangeSet(order = ORDER_004, id = "metaConfiguration", author = "enaiand")
    public void metadataWithConfiguration(Jongo jongo) {
        MongoCollection collectionMetadata = jongo.getCollection(COLLECTION_METADATA.getName());

        CollectionMetadata job = emptyMetadata(JOB.getName());

        job.getFields().addAll(newArrayList(
            createField("id", "ID", STRING),
            createField("name", "Name", STRING),
            createField("context", "Context", STRING),
            createField("modifiedDate", "Modified Date", DATE_TIME),
            createField("testSessionCount", "Test Session Count", NUMBER_SHORT),
            createField("lastTestSessionTestSuiteCount", "Last Test Session Test Suite Count", NUMBER_SHORT),
            createField("lastTestSessionTestCaseCount", "Last Test Session Test Case Count", NUMBER_SHORT),
            createField("lastExecutionDate", "Last Execution Date", DATE_TIME),
            createField("lastTestSessionDuration", "Last Test Session Duration", DURATION),
            createField("avgTestSessionDuration", "Average Test Session Duration", DURATION)
        ));
        upsert(collectionMetadata, job);

        CollectionMetadata session = emptyMetadata(TEST_SESSION.getName());
        session.getFields().addAll(newArrayList(
            createField("id", "ID", STRING),
            createField("time.startDate", "Start Date", DATE_TIME),
            createField("time.stopDate", "Stop Date", DATE_TIME),
            createField("time.duration", "Duration", DURATION),
            createField("uri", "URI", LINK),
            createField("logReferences", "Log References", STRING),
            createField("resultCode", "Status", STATUS),
            createField("lastExecutionDate", "Last Execution Date", DATE_TIME),
            createField("testCaseCount", "Test Case Count", NUMBER_SHORT),
            createField("testSuiteCount", "Test Suite Count", NUMBER_SHORT),
            createField("passRate", "Pass Rate", RATE),
            createField("modifiedDate", "Modified Date", DATE_TIME),
            createField("createdDate", "Created Date", DATE_TIME)
        ));
        upsert(collectionMetadata, session);

        CollectionMetadata suite = emptyMetadata(TEST_SUITE_RESULT.getName());
        suite.getFields().addAll(newArrayList(
            createField("id", "ID", STRING),
            createField("time.startDate", "Start Date", DATE_TIME),
            createField("time.stopDate", "Stop Date", DATE_TIME),
            createField("time.duration", "Duration", DURATION),
            createField("statistics.total", "Total", NUMBER_SHORT),
            createField("statistics.passed", "Passed", STATUS_SUCCESS),
            createField("statistics.pending", "Pending", STATUS_PENDING),
            createField("statistics.cancelled", "Cancelled", STATUS_CANCELLED),
            createField("statistics.failed", "Failed", STATUS_BROKEN),
            createField("statistics.broken", "Broken", STATUS_BROKEN),
            createField("passRate", "Pass Rate", RATE),
            createField("modifiedDate", "Modified Date", DATE_TIME),
            createField("createdDate", "Created Date", DATE_TIME)
        ));
        upsert(collectionMetadata, suite);

        CollectionMetadata testCase = emptyMetadata(TEST_CASE_RESULT.getName());
        testCase.getFields().addAll(newArrayList(
            createField("id", "ID", STRING),
            createField("name", "Name", STRING),
            createField("time.startDate", "Start Date", DATE_TIME),
            createField("time.stopDate", "Stop Date", DATE_TIME),
            createField("time.duration", "Duration", DURATION),
            createField("resultCode", "Status", STATUS),
            createField("modifiedDate", "Modified Date", DATE_TIME),
            createField("createdDate", "Created Date", DATE_TIME)
        ));
        upsert(collectionMetadata, testCase);
    }

    @ChangeSet(order = ORDER_005, id = "renameFields", author = "erusbob")
    public void renameAdditionlFields(Jongo jongo) {
        MongoCollection testSessions = jongo.getCollection(TEST_SESSION.getName());
        testSessions.update("{}").multi().with("{ $rename: { 'JEKINS_JOB_NAME_FLD': 'JENKINS_JOB_NAME' }}");
        testSessions.update("{}").multi().with("{ $rename: { 'ISO_VERSION_FLD': 'ISO_VERSION' }}");
        testSessions.update("{}").multi().with("{ $rename: { 'ISO_ARTIFACT_ID_FLD': 'ISO_ARTIFACT_ID' }}");
        testSessions.update("{}").multi().with("{ $rename: { 'DROP_NAME_FLD': 'DROP_NAME' }}");
    }

    @ChangeSet(order = ORDER_006, id = "moveMetadata", author = "enaiand")
    public void updateMeta(Jongo jongo) {
        MongoCollection collectionMetadata = jongo.getCollection(COLLECTION_METADATA.getName());
        collectionMetadata.update("{}").multi().with("{$unset: { keys: ''}}");
        collectionMetadata.update("{name: 'testSession'}")
            .with("{$push: { fields: { 'field': 'JENKINS_JOB_NAME', title: 'Jenkins Job Name', type: 'String'}}}");
        collectionMetadata.update("{name: 'testSession'}")
            .with("{$push: { fields: { 'field': 'ISO_VERSION', title: 'ISO Version', type: 'String'}}}");
        collectionMetadata.update("{name: 'testSession'}")
            .with("{$push: { fields: { 'field': 'ISO_ARTIFACT_ID', title: 'ISO Artifact ID', type: 'String'}}}");
        collectionMetadata.update("{name: 'testSession'}")
            .with("{$push: { fields: { 'field': 'DROP_NAME', title: 'Drop Name', type: 'String'}}}");
    }

    @ChangeSet(order = ORDER_007, id = "addPosition", author = "eserish")
    public void addPositionToMeta(Jongo jongo) {
        MongoCollection collectionMetadata = jongo.getCollection(COLLECTION_METADATA.getName());
        //remove all duplicates
        collectionMetadata.update("{name: 'testSession'}").multi()
            .with("{$pull: {fields: { field: {$in: " +
                "['JENKINS_JOB_NAME', 'ISO_VERSION', 'ISO_ARTIFACT_ID', 'DROP_NAME']}}}}");

        //recreate additional fields
        collectionMetadata.update("{name: 'testSession'}")
            .with("{$push: { fields: { 'field': 'JENKINS_JOB_NAME', title: 'Jenkins Job Name', type: 'String'}}}");
        collectionMetadata.update("{name: 'testSession'}")
            .with("{$push: { fields: { 'field': 'ISO_VERSION', title: 'ISO Version', type: 'String'}}}");
        collectionMetadata.update("{name: 'testSession'}")
            .with("{$push: { fields: { 'field': 'ISO_ARTIFACT_ID', title: 'ISO Artifact ID', type: 'String'}}}");
        collectionMetadata.update("{name: 'testSession'}")
            .with("{$push: { fields: { 'field': 'DROP_NAME', title: 'Drop Name', type: 'String'}}}");
    }

    @ChangeSet(order = ORDER_008, id = "addImportStatusFieldWithDefaultValue", author = "erusbob")
    public void addImportStatusFieldWithDefaultValue(Jongo jongo) {
        MongoCollection testCases = jongo.getCollection(TEST_CASE_RESULT.getName());
        testCases.update("{ importStatus: { $exists: false } }")
            .multi()
            .with("{$set : {\"importStatus\" : \"PENDING\"}}"); // to import
    }

    @ChangeSet(order = ORDER_009, id = "changeFieldNameForTestSessionMeta", author = "eserish")
    public void changeFieldName(Jongo jongo) {
        MongoCollection testSessions = jongo.getCollection(COLLECTION_METADATA.getName());
        testSessions.update("{name: 'testSession'}")
            .with("{$pull: {fields: { field: 'id'}}}");
        testSessions.update("{name: 'testSession'}")
            .with("{$push: {fields: {$each: [{ 'field': 'executionId', title: 'ID', type: 'String'}], $position: 0}}}");
    }

    @ChangeSet(order = ORDER_010, id = "addEnmTribeContexts", author = "enaiand")
    public void addEnmTribeContexts(Jongo jongo) {
        MongoCollection jobConfiguration = jongo.getCollection(JOB_CONFIGURATION.getName());
        jobConfiguration.insert("{'jobName': '^RFA_Job$','source': 'ENM'," +
            "'contextId': '55d4b2ac-e9e1-11e5-9ce9-5e5517507c66','priority': 100}");
        jobConfiguration.insert("{'jobName': '.*_RFA_250$','source': 'ENM'," +
            "'contextId': '55d4b2ac-e9e1-11e5-9ce9-5e5517507c66','priority': 100}");
        jobConfiguration.insert("{'jobName': '^Deploy.*[0-9]+_KGB.*','source': 'ENM'," +
            "'contextId': '55d4bdd8-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '.*(?i)ERICPol(?-i).*[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4bf7c-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^AP[_-]?Tribe[_-]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4b4d2-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^CM[_-]?Tribe[_-]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4b5d6-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^FM[_-]?Tribe[_-]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4b6a8-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^PM[_-]?Tribe[_-]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4b770-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^CM[_-]?Tribe[_-]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4b82e-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^Auto[_-]?Provisioning.*[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4bcd4-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^ENMeshed[_-]?Security[_-]?Tribe[_-]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4beaa-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '.*(?i)ERICPol(?-i).*[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4bf7c-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^Insert[_-]?Name[_-]?Here[_-]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4c0ee-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^KVM[-_]?Physical[-_]?Rollout[_-]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4c1ac-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^Key[-_]?Components.*[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4c562-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^Legolas[-_]?Key[-_]?Components.*[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4c634-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^Maintrack[-_]?Tribe[-_]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4c6f2-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^Monitoring[-_]?Tribe[-_]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4c7b0-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^Oceans[-_]?Deployment.*[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4c882-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^SHM[-_]Tribe[-_]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4c940-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^Security[-_]?Tribe[-_]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4ca4e-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^SkyFall[-_]?Security[-_]?Tribe[-_]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4cddc-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^TEI[-_]?Security[-_]?Tribe[-_]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4ceae-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^Transport[-_]?Tribe[-_]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4cf6c-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^UTF[-_]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4d02a-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '^Whitestar[-_]?RV[-_]?Tribe[-_]?[0-9]+_KGB.*'," +
            "'source': 'ENM','contextId': '55d4d0e8-e9e1-11e5-9ce9-5e5517507c66','priority': 90}");
        jobConfiguration.insert("{'jobName': '.*RFA.*','source': 'ENM'," +
            "'contextId': '55d4d2e8-e9e1-11e5-9ce9-5e5517507c66','priority': 80}");
        jobConfiguration.insert("{'jobName': '.*','source': 'ENM'," +
            "'contextId': '55d4d1e8-e9e1-11e5-9ce9-5e5517507c66','priority': 70}");
    }

    @ChangeSet(order = ORDER_011, id = "changeFieldNameForTestSuiteMeta", author = "eserish")
    public void changeSuiteFieldName(Jongo jongo) {
        MongoCollection testSessions = jongo.getCollection(COLLECTION_METADATA.getName());
        testSessions.update("{name: 'testSuiteResult'}")
            .with("{$pull: {fields: { field: 'id'}}}");
        testSessions.update("{name: 'testSuiteResult'}")
            .with("{$push: {fields: {$each: [{ 'field': 'name', title: 'ID', type: 'String'}], $position: 0}}}");
    }

    @ChangeSet(order = ORDER_013, id = "addTestCaseResultMetadataFields", author = "erusbob")
    public void addTestCaseResultMetadataFields(Jongo jongo) {
        MongoCollection collectionMetadata = jongo.getCollection(COLLECTION_METADATA.getName());
        collectionMetadata.update("{}").multi().with("{$unset: { keys: ''}}");

        collectionMetadata.update("{name: 'testCaseResult'}")
            .with("{$push: { fields: { 'field': 'REQUIREMENTS', title: 'Requirements', type: 'String'}}}");
        collectionMetadata.update("{name: 'testCaseResult'}")
            .with("{$push: { fields: { 'field': 'GROUPS', title: 'Groups', type: 'String'}}}");
        collectionMetadata.update("{name: 'testCaseResult'}")
            .with("{$push: { fields: { 'field': 'PRIORITY', title: 'Priority', type: 'String'}}}");
        collectionMetadata.update("{name: 'testCaseResult'}")
            .with("{$push: { fields: { 'field': 'COMPONENT', title: 'Component', type: 'String'}}}");
        collectionMetadata.update("{name: 'testCaseResult'}")
            .with("{$push: { fields: { 'field': 'ISO_VERSION', title: 'ISO Version', type: 'String'}}}");
        collectionMetadata.update("{name: 'testCaseResult'}")
            .with("{$push: { fields: { 'field': 'DROP_NAME', title: 'Drop Name', type: 'String'}}}");
        collectionMetadata.update("{name: 'testCaseResult'}")
            .with("{$push: { fields: { 'field': 'TITLE', title: 'Title', type: 'String'}}}");
    }

    @ChangeSet(order = ORDER_015, id = "changeColumns", author = "eserish")
    public void changeColumnNames(Jongo jongo) {
        MongoCollection collectionMetadata = jongo.getCollection(COLLECTION_METADATA.getName());
        collectionMetadata.update("{name: 'job'}")
            .with("{$pull: {fields: { field: 'lastTestSessionTestSuiteCount'}}}");
        collectionMetadata.update("{name: 'job'}")
            .with("{$pull: {fields: { field: 'lastTestSessionTestCaseCount'}}}");
        collectionMetadata.update("{name: 'job'}")
            .with("{$pull: {fields: { field: 'lastTestSessionDuration'}}}");

        collectionMetadata.update("{name: 'job'}")
            .with("{$push: { fields: { 'field': 'lastTestSessionTestSuiteCount', " +
                "title: 'Last Session Test Suite Count', type: 'String'}}}");
        collectionMetadata.update("{name: 'job'}")
            .with("{$push: { fields: { 'field': 'lastTestSessionTestCaseCount', " +
                "title: 'Last Session Test Case Count', type: 'String'}}}");
        collectionMetadata.update("{name: 'job'}")
            .with("{$push: { fields: { 'field': 'lastTestSessionDuration', " +
                "title: 'Last Session Duration', type: 'String'}}}");
    }

    @ChangeSet(order = ORDER_016, id = "addContextIdToProjectRequirement", author = "erusbob")
    public void addContextIdToProjectRequirement(Jongo jongo) {
        MongoCollection testCases = jongo.getCollection(PROJECT_REQUIREMENT.getName());
        testCases.update("{ contextId: { $exists: false } }")
            .multi()
            .with("{$set: {\"contextId\" : null}}");
    }

    @ChangeSet(order = ORDER_017, id = "changeDurationColumnType", author = "eserish")
    public void changeDurationColumnType(Jongo jongo) {
        MongoCollection collectionMetadata = jongo.getCollection(COLLECTION_METADATA.getName());
        collectionMetadata.update("{name: 'job'}")
            .with("{$pull: {fields: { field: 'lastTestSessionDuration'}}}");

        collectionMetadata.update("{name: 'job'}")
            .with("{$push: { fields: { 'field': 'lastTestSessionDuration', " +
                "title: 'Last Session Duration', type: 'Duration'}}}");
    }

    @ChangeSet(order = ORDER_018, id = "fixColumnTypes", author = "enikarh")
    public void fixColumnTypes(Jongo jongo) {
        MongoCollection collectionMetadata = jongo.getCollection(COLLECTION_METADATA.getName());

        collectionMetadata.update("{name: 'job'}")
            .with("{$pull: {fields: { field: 'lastTestSessionTestSuiteCount'}}}");
        collectionMetadata.update("{name: 'job'}")
            .with("{$push: { fields: { 'field': 'lastTestSessionTestSuiteCount', " +
                "title: 'Last Session Test Suite Count', type: 'NumberShort'}}}");

        collectionMetadata.update("{name: 'job'}")
            .with("{$pull: {fields: { field: 'lastTestSessionTestCaseCount'}}}");
        collectionMetadata.update("{name: 'job'}")
            .with("{$push: { fields: { 'field': 'lastTestSessionTestCaseCount', " +
                "title: 'Last Session Test Case Count', type: 'NumberShort'}}}");
    }

    @ChangeSet(order = ORDER_019, id = "addIgnoredFlag", author = "evlailj")
    public void addIgnoredFlag(Jongo jongo) {
        MongoCollection collectionMetadata = jongo.getCollection(COLLECTION_METADATA.getName());

        collectionMetadata.update("{name: 'testSession'}")
            .with("{$push: { fields: { 'field': 'ignored', " +
                "title: 'Ignored', type: 'Boolean'}}}");
    }

    @ChangeSet(order = ORDER_020, id = "addResultCodeMappings", author = "evlailj")
    public void addResultCodeMappings(Jongo jongo) {
        MongoCollection resultCode = jongo.getCollection(RESULT_CODE.getName());
        String query = "{source: #, externalCode: #, internalCode: #}";

        resultCode.insert(query, TAF_TMS, "NOT_STARTED", PENDING);
        resultCode.insert(query, TAF_TMS, "PASS", PASSED);
        resultCode.insert(query, TAF_TMS, "PASSED_WITH_EXCEPTION", PASSED);
        resultCode.insert(query, TAF_TMS, "FAIL", FAILED);
        resultCode.insert(query, TAF_TMS, "WIP", PENDING);
        resultCode.insert(query, TAF_TMS, "BLOCKED", CANCELLED);

        resultCode.insert(query, TAF_EIFFEL, "SUCCESS", PASSED);
        resultCode.insert(query, TAF_EIFFEL, "FAILURE", FAILED);
        resultCode.insert(query, TAF_EIFFEL, "ERROR", BROKEN);
        resultCode.insert(query, TAF_EIFFEL, "ABORTED", CANCELLED);
        resultCode.insert(query, TAF_EIFFEL, "NOT_BUILT", CANCELLED);
        resultCode.insert(query, TAF_EIFFEL, "NOT_SET", PENDING);
    }

    @ChangeSet(order = ORDER_021, id = "changeComponentFieldToList", author = "evlailj")
    public void changeComponentFieldToList(Jongo jongo) throws IOException {
        String js = readResource("mongo/data/migrations/021-component-field-to-list.js");
        jongo.runCommand("{ eval: # }", js)
            .throwOnError()
            .map(new RawResultHandler<>());

        MongoCollection collectionMetadata = jongo.getCollection(COLLECTION_METADATA.getName());
        collectionMetadata.update("{name: 'testCaseResult'}")
            .with("{$pull: {fields: { field: 'COMPONENT'}}}");
        collectionMetadata.update("{name: 'testCaseResult'}")
            .with("{$push: { fields: { 'field': 'COMPONENTS', title: 'Components', type: 'String'}}}");
    }

    @ChangeSet(order = ORDER_022, id = "updateTestSessionIsoVersions", author = "eniakel")
    public void updateIsoVersionsForSorting(Jongo jongo) {
        String isoVersionField = "ISO_VERSION";
        List<String> collections = new ArrayList<>(
            Arrays.asList(TEST_SESSION.getName(), TEST_CASE_RESULT.getName()));

        for (String collectionName : collections) {
            MongoCollection collection = jongo.getCollection(collectionName);
            DBCursor cursor = collection.getDBCollection()
                .find(new BasicDBObject(), new BasicDBObject(isoVersionField, 1));

            while (cursor.hasNext()) {
                DBObject dbObject = cursor.next();
                String isoVersion = (String) dbObject.get(isoVersionField);
                String paddedIsoVersion = VersionConverter.getPaddedVersion(isoVersion);
                if (paddedIsoVersion != null) {
                    ObjectId objectId = (ObjectId) dbObject.get("_id");
                    collection.update("{_id: #}", objectId)
                        .with("{$set:{ ISO_VERSION_PADDED: #}}", paddedIsoVersion);
                }
            }
        }
    }

    @ChangeSet(order = ORDER_023, id = "addIsoAndDropDataToSuites", author = "eniakel")
    public void addIsoAndDropDataToSuites(Jongo jongo) {
        MongoCollection testSessionCollection = jongo.getCollection(TEST_SESSION.getName());
        MongoCollection testSuiteResultCollection = jongo.getCollection(TEST_SUITE_RESULT.getName());
        DBCursor testSuiteCursor = testSuiteResultCollection.getDBCollection().find();

        while (testSuiteCursor.hasNext()) {
            DBObject dbObject = testSuiteCursor.next();
            String testSessionId = (String) dbObject.get("testSessionId");
            DBObject testSession = testSessionCollection.getDBCollection()
                .findOne(new BasicDBObject("_id", new ObjectId(testSessionId)));

            if (testSession == null) {
                continue;
            }

            if (testSession.get("ISO_VERSION") != null) {
                testSuiteResultCollection.update("{testSessionId: #}", testSessionId)
                    .with("{$set:{ ISO_VERSION: #}}", testSession.get("ISO_VERSION"));
            }
            if (testSession.get("ISO_VERSION_PADDED") != null) {
                testSuiteResultCollection.update("{testSessionId: #}", testSessionId)
                    .with("{$set:{ ISO_VERSION_PADDED: #}}", testSession.get("ISO_VERSION_PADDED"));
            }
            if (testSession.get("DROP_NAME") != null) {
                String dropName = testSession.get("DROP_NAME").toString();
                testSuiteResultCollection.update("{testSessionId: #}", testSessionId)
                    .with("{$set:{ DROP_NAME: #}}", dropName);
            }
        }
    }

    private void upsert(MongoCollection collection, CollectionMetadata metadata) {
        collection.update("{name:#}", metadata.getName())
            .upsert()
            .with("{$set: {fields:#}}", metadata.getFields());
    }

    private FieldMetadata createField(String field, String title, FieldType type) {
        FieldMetadata fieldMetadata = new FieldMetadata();
        fieldMetadata.setField(field);
        fieldMetadata.setTitle(title);
        fieldMetadata.setType(type);

        return fieldMetadata;
    }

    private CollectionMetadata emptyMetadata(String collection) {
        CollectionMetadata metadata = new CollectionMetadata();
        metadata.setName(collection);

        return metadata;
    }

    private String readResource(String resourceName) throws IOException {
        return String.join("\n", readLines(getResource(resourceName), UTF_8));
    }
}
