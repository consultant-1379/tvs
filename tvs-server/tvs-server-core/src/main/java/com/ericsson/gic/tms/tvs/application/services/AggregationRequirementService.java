package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.domain.model.verdict.AdditionalFieldAware;
import com.ericsson.gic.tms.tvs.domain.model.verdict.ProjectRequirementType;
import com.ericsson.gic.tms.tvs.domain.repositories.ProjectRequirementRepository;
import com.ericsson.gic.tms.tvs.infrastructure.MongoQueryService;
import org.jongo.Aggregate;
import org.jongo.Jongo;
import org.jongo.marshall.jackson.oid.MongoId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;
import static com.ericsson.gic.tms.tvs.infrastructure.TvsQueryFile.*;
import static com.google.common.collect.Lists.*;
import static java.util.stream.Collectors.*;

@Service
public class AggregationRequirementService {

    @Autowired
    private Jongo jongo;

    @Autowired
    private ProjectRequirementRepository repository;

    @Autowired
    private MongoQueryService queryService;

    public void aggregateUserStories() {
        Aggregate.ResultsIterator<Map> result = getMatchedEntries(ProjectRequirementType.STORY);

        List<Object> userStoryIds = newArrayList(result.iterator()).stream()
            .map(a -> a.get("id")).collect(toList());

        Aggregate.ResultsIterator<AggregationResult> testCaseResults = jongo.getCollection(TEST_CASE_RESULT.getName())
            .aggregate(queryService.getQuery(REQ_US_MATCH), userStoryIds)
            .and(queryService.getQuery(REQ_US_SORT))
            .and(queryService.getQuery(REQ_US_GROUP1))
            .and(queryService.getQuery(REQ_US_UNWIND))
            .and(queryService.getQuery(REQ_US_GROUP2))
            .and(queryService.getQuery(REQ_US_PROJECT))
            .as(AggregationResult.class);

        saveAggregatedValues(testCaseResults.iterator());
    }

    public void aggregateEpics() {
        Aggregate.ResultsIterator<Map> result = getMatchedEntries(ProjectRequirementType.EPIC);

        result.iterator().forEachRemaining(entry -> {
            Aggregate.ResultsIterator<AggregationResult> testCaseResults =
                jongo.getCollection(PROJECT_REQUIREMENT.getName())
                    .aggregate(queryService.getQuery(REQ_EPICS_MATCH), entry.get("children"))
                    .and(queryService.getQuery(REQ_EPICS_GROUP))
                    .and(queryService.getQuery(REQ_EPICS_PROJECT))
                    .as(AggregationResult.class);

            saveAggregatedValues((String) entry.get("id"), testCaseResults.iterator());
        });
    }

    public void aggregateMainRequirements() {
        Aggregate.ResultsIterator<Map> result = getMatchedEntries(ProjectRequirementType.MR);

        result.iterator().forEachRemaining(entry -> {
            Aggregate.ResultsIterator<AggregationResult> testCaseResults =
                jongo.getCollection(PROJECT_REQUIREMENT.getName())
                    .aggregate(queryService.getQuery(REQ_MR_MATCH), entry.get("children"))
                    .and(queryService.getQuery(REQ_MR_GROUP))
                    .and(queryService.getQuery(REQ_MR_PROJECT))
                    .as(AggregationResult.class);

            saveAggregatedValues((String) entry.get("id"), testCaseResults.iterator());
        });
    }

    private Aggregate.ResultsIterator<Map> getMatchedEntries(ProjectRequirementType reqType) {
        return jongo.getCollection(PROJECT_REQUIREMENT.getName())
            .aggregate(queryService.getQuery(REQ_MATCH), reqType.name())
            .and(queryService.getQuery(REQ_PROJECT))
            .as(Map.class);
    }

    private void saveAggregatedValues(Iterator<AggregationResult> values) {
        values.forEachRemaining(map -> repository.saveAdditional(map.getId(), map.getAdditionalFields()));
    }

    private void saveAggregatedValues(final String parentId, Iterator<AggregationResult> values) {
        values.forEachRemaining(map -> repository.saveAdditional(parentId, map.getAdditionalFields()));
    }

    private static final class AggregationResult extends AdditionalFieldAware {
        @MongoId
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
