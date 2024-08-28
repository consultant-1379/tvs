package com.ericsson.gic.tms.tvs.domain.repositories;

import com.ericsson.gic.tms.tvs.domain.model.verdict.CollectionMetadata;
import org.springframework.stereotype.Repository;

import static com.ericsson.gic.tms.tvs.domain.model.TvsCollections.*;

@Repository
public class CollectionMetadataRepository extends BaseJongoRepository<CollectionMetadata, String> {

    protected CollectionMetadataRepository() {
        super(CollectionMetadata.class, "name");
    }

    @Override
    protected String getCollectionName() {
        return COLLECTION_METADATA.getName();
    }

    public void upsert(CollectionMetadata collectionMetadata) {
        getCollection()
            .update("{name:#}", collectionMetadata.getName())
            .upsert()
            .with("{$set: {fields:#}}", collectionMetadata.getFields());
    }

}
