package com.kLagan.challenge.infraestructure.persistence;

import com.kLagan.challenge.domain.model.Asset;
import com.kLagan.challenge.application.port.AssetRepository;
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
            String sortDirection) {
        
        Query query = new Query();
        
        if (filename != null && !filename.isBlank()) {
            query.addCriteria(Criteria.where("filename").regex(filename, "i"));
        }
        
        if (filetype != null && !filetype.isBlank()) {
            query.addCriteria(Criteria.where("contentType").is(filetype));
        }
        
        if (uploadDateStart != null && uploadDateEnd != null) {
            query.addCriteria(Criteria.where("uploadDate")
                    .gte(uploadDateStart)
                    .lte(uploadDateEnd));
        }
        
        if ("DESC".equalsIgnoreCase(sortDirection)) {
            query.with(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "uploadDate"));
        } else {
            query.with(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.ASC, "uploadDate"));
        }
        
        return mongoTemplate.find(query, Asset.class);
    }

    @Override
    public Mono<Asset> findById(String id) {
        return mongoTemplate.findById(id, Asset.class);
    }

    @Override
    public Mono<Void> updateStatus(String assetId, String status) {
        return mongoTemplate.updateFirst(
            Query.query(Criteria.where("id").is(assetId)),
            Update.update("status", status),
            Asset.class
        ).then();
    }

    @Override
    public Mono<Void> updateUrl(String assetId, String url) {
        return mongoTemplate.updateFirst(
            Query.query(Criteria.where("id").is(assetId)),
            Update.update("url", url),
            Asset.class
        ).then();
    }
}