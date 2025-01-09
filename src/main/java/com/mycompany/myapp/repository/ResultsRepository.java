package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Results;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data MongoDB reactive repository for the Results entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResultsRepository extends ReactiveMongoRepository<Results, String> {
    @Query("{}")
    Flux<Results> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    Flux<Results> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Mono<Results> findOneWithEagerRelationships(String id);
}
