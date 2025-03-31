package com.kLagan.challenge.infraestructure.persistence;

import com.kLagan.challenge.domain.model.Asset;
import com.kLagan.challenge.application.port.AssetRepository;
import com.kLagan.challenge.application.util.AssetConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Repository
public class MongoAssetRepository implements AssetRepository {

    private static final Logger logger = LoggerFactory.getLogger(MongoAssetRepository.class);
    private final ReactiveMongoTemplate mongoTemplate;

    public MongoAssetRepository(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Asset> save(Asset asset) {
        return mongoTemplate.save(asset);
    }

    @Override
    public Flux<Asset> search(
            LocalDateTime uploadDateStart,
            LocalDateTime uploadDateEnd,
            String filename,
            String filetype,
            String status,
            String sortDirection) {
        
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (status != null && !status.isBlank()) {
            criteria.and(AssetConstants.FIELD_STATUS).is(status);
        }
        
        if (filename != null && !filename.isBlank()) {
            criteria.and(AssetConstants.FIELD_FILENAME).regex(filename, "i");
        }
        
        if (filetype != null && !filetype.isBlank()) {
            criteria.and(AssetConstants.FIELD_CONTENT_TYPE).is(filetype);
        }
        
        if (uploadDateStart != null || uploadDateEnd != null) {
            Criteria dateCriteria = new Criteria(AssetConstants.FIELD_UPLOAD_DATE);
            if (uploadDateStart != null) {
                dateCriteria.gte(uploadDateStart);
            }
            if (uploadDateEnd != null) {
                dateCriteria.lte(uploadDateEnd);
            }
            criteria.andOperator(dateCriteria);
        }
        
        query.addCriteria(criteria);
        
        Sort.Direction direction = Sort.Direction.ASC;
        if (AssetConstants.SORT_DESC.equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.DESC;
        }
        query.with(Sort.by(direction, AssetConstants.FIELD_UPLOAD_DATE));
        
        logger.debug("Executing query: {}", query);
        
        return mongoTemplate.find(query, Asset.class);
    }

    @Override
    public Mono<Asset> findById(String id) {
        return mongoTemplate.findById(id, Asset.class);
    }

    @Override
    public Mono<Void> updateStatus(String assetId, String status) {
        return mongoTemplate.updateFirst(
            Query.query(Criteria.where(AssetConstants.FIELD_ID).is(assetId)),
            Update.update(AssetConstants.FIELD_STATUS, status),
            Asset.class
        ).then();
    }

    @Override
    public Mono<Void> updateUrl(String assetId, String url) {
        return mongoTemplate.updateFirst(
            Query.query(Criteria.where(AssetConstants.FIELD_ID).is(assetId)),
            Update.update(AssetConstants.FIELD_URL, url),
            Asset.class
        ).then();
    }
}
