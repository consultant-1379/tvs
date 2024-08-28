package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.domain.model.MongoEntity;
import com.ericsson.gic.tms.tvs.domain.model.verdict.AdditionalFieldAware;
import com.ericsson.gic.tms.tvs.domain.model.verdict.CollectionMetadata;
import com.ericsson.gic.tms.tvs.domain.model.verdict.FieldMetadata;
import com.ericsson.gic.tms.tvs.domain.model.verdict.FieldType;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Set;

import static java.util.stream.Collectors.*;

public abstract class ExtensibleJongoRepository<T extends AdditionalFieldAware & MongoEntity<ID>,
    ID extends Serializable> extends BaseJongoRepository<T, ID> {

    @Autowired
    private CollectionMetadataRepository collectionMetadataRepository;

    protected ExtensibleJongoRepository(Class<T> type, String idFieldName) {
        super(type, idFieldName);
    }

    protected ExtensibleJongoRepository(Class<T> type) {
        super(type);
    }

    @Override
    public T save(T entity) {
        CollectionMetadata metadata = collectionMetadataRepository.findOne(getCollectionName());

        Set<String> savedFieldNames = metadata.getFields().stream()
            .map(FieldMetadata::getField)
            .collect(toSet());

        Sets.SetView<String> difference = Sets.difference(entity.getAdditionalFields().keySet(), savedFieldNames);

        if (!difference.isEmpty()) {
            difference.forEach(field -> {
                FieldMetadata fieldMetadata = new FieldMetadata();
                fieldMetadata.setField(field);
                fieldMetadata.setType(FieldType.STRING);
                // TODO Title Case is needed
                fieldMetadata.setTitle(field);

                metadata.getFields().add(fieldMetadata);
            });
            collectionMetadataRepository.upsert(metadata);
        }

        return super.save(entity);
    }
}
