package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.AbstractIntegrationTest;
import com.ericsson.gic.tms.tvs.domain.model.verdict.CollectionMetadata;
import com.ericsson.gic.tms.tvs.domain.model.verdict.FieldMetadata;
import com.ericsson.gic.tms.tvs.domain.model.verdict.TestSession;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.TEST_CASE_RESULT;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

public class MetadataRepositoryTest extends AbstractIntegrationTest {

    private static final String TITLE_FLD = "TITLE";
    private static final String DROP_NAME_FLD = "DROP_NAME";
    private static final String ISO_VERSION_FLD = "ISO_VERSION";
    private static final String COMPONENTS_FLD = "COMPONENTS";
    private static final String PRIORITY_FLD = "PRIORITY";
    private static final String GROUPS_FLD = "GROUPS";
    private static final String REQUIREMENTS_FLD = "REQUIREMENTS";

    @Autowired
    private TestSessionRepository testSessionRepository;

    @Autowired
    private CollectionMetadataRepository collectionMetadataRepository;

    @Before
    public void setUp() {
        TestSession session = new TestSession();
        session.addAdditionalFields("Field 1", 1);
        session.addAdditionalFields("Field 2", 1);
        session.addAdditionalFields("Field 3", 1);

        testSessionRepository.save(session);
    }

    @Test
    public void testAdditionalFieldMetaSave() {
        CollectionMetadata metadata = collectionMetadataRepository.findOne(testSessionRepository.getCollectionName());
        assertThat(metadata)
            .isNotNull();

        assertThat(metadata.getFields())
            .extracting(FieldMetadata::getField)
            .contains("Field 1", "Field 2", "Field 3");
    }

    @Test
    public void testDuplicateAdditionalFieldMetaSave() {
        TestSession session = new TestSession();
        session.addAdditionalFields("Field 1", 2);
        session.addAdditionalFields("Unique Field", 3);

        testSessionRepository.save(session);

        CollectionMetadata metadata = collectionMetadataRepository.findOne(testSessionRepository.getCollectionName());
        assertThat(metadata)
            .isNotNull();

        assertThat(metadata.getFields())
            .extracting(FieldMetadata::getField)
            .contains("Field 1", "Field 2", "Field 3", "Unique Field");

        List<String> usedFieldNames = metadata.getFields().stream()
            .map(FieldMetadata::getField)
            .collect(toList());

        Set<String> duplicateKeys = usedFieldNames.stream()
            .filter(field -> Collections.frequency(usedFieldNames, field) > 1)
            .collect(toSet());

        assertThat(duplicateKeys)
            .hasSize(0);
    }

    @Test
    public void testTestCaseAdditionalFieldMeta() {
        CollectionMetadata metadata = collectionMetadataRepository.findOne(TEST_CASE_RESULT.getName());
        assertThat(metadata)
            .isNotNull();

        assertThat(findFieldByNameAndTitle(metadata, TITLE_FLD, "Title"))
            .as(TITLE_FLD)
            .isNotNull();

        assertThat(findFieldByNameAndTitle(metadata, DROP_NAME_FLD, "Drop Name"))
            .as(DROP_NAME_FLD)
            .isNotNull();

        assertThat(findFieldByNameAndTitle(metadata, ISO_VERSION_FLD, "ISO Version"))
            .as(ISO_VERSION_FLD)
            .isNotNull();

        assertThat(findFieldByNameAndTitle(metadata, COMPONENTS_FLD, "Components"))
            .as(COMPONENTS_FLD)
            .isNotNull();

        assertThat(findFieldByNameAndTitle(metadata, PRIORITY_FLD, "Priority"))
            .as(PRIORITY_FLD)
            .isNotNull();

        assertThat(findFieldByNameAndTitle(metadata, GROUPS_FLD, "Groups"))
            .as(GROUPS_FLD)
            .isNotNull();

        assertThat(findFieldByNameAndTitle(metadata, REQUIREMENTS_FLD, "Requirements"))
            .as(REQUIREMENTS_FLD)
            .isNotNull();
    }

    private FieldMetadata findFieldByNameAndTitle(CollectionMetadata metadata, String name, String title) {
        return metadata.getFields().stream()
            .filter(
                field ->
                    Objects.equals(field.getField(), name)
                        && Objects.equals(field.getTitle(), title))
            .findAny().orElse(null);
    }
}
