package com.ericsson.gic.tms.tvs.application.services;

import com.ericsson.gic.tms.tvs.domain.model.verdict.CollectionMetadata;
import com.ericsson.gic.tms.tvs.domain.model.verdict.FieldMetadata;
import com.ericsson.gic.tms.tvs.domain.repositories.CollectionMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionMetadataService {

    @Autowired
    private CollectionMetadataRepository metadataRepository;

    public List<FieldMetadata> getColumns(String collection) {
        CollectionMetadata metadata = metadataRepository.findOne(collection);

        //TODO filter contexts by current

        return metadata.getFields();
    }
}
