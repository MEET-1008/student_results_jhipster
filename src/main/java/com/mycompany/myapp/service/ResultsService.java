package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Results;
import com.mycompany.myapp.domain.enumeration.pass_faill;
import com.mycompany.myapp.repository.ResultsRepository;
import java.util.List;

import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Results}.
 */
@Service
public class ResultsService {

    private final Logger log = LoggerFactory.getLogger(ResultsService.class);

    private final ResultsRepository resultsRepository;

    private final UserRepository userRepository;


    public ResultsService(ResultsRepository resultsRepository, UserRepository userRepository) {
        this.resultsRepository = resultsRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save a results.
     *
     * @param results the entity to save.
     * @return the persisted entity.
     */
    public Mono<Results> save(Results results) {
        log.debug("Request to save Results : {}", results);
        return SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(user -> {

                int java = Integer.parseInt(results.getJava());
                int python = Integer.parseInt(results.getPython());
                int flutter = Integer.parseInt(results.getFlutter());

                double sum = java + python + flutter;

                results.setUser(user);
                results.setStudent_id(user.getId());
                results.setTotal_marks(sum);
                results.setAvg(sum/3);
                if (java < 33 || python < 33 || flutter < 33) {
                    results.setResult(pass_faill.FAIL);
                } else {
                    results.setResult(pass_faill.PASS);
                }

                return resultsRepository.save(results);
            });    }


    /**
     * Update a results.
     *
     * @param results the entity to save.
     * @return the persisted entity.
     */
    public Mono<Results> update(Results results) {
        log.debug("Request to save Results : {}", results);

        return resultsRepository.save(results);
    }

    /**
     * Partially update a results.
     *
     * @param results the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Results> partialUpdate(Results results) {
        log.debug("Request to partially update Results : {}", results);

        return resultsRepository
            .findById(results.getId())
            .map(existingResults -> {
                if (results.getJava() != null) {
                    existingResults.setJava(results.getJava());
                }
                if (results.getPython() != null) {
                    existingResults.setPython(results.getPython());
                }
                if (results.getFlutter() != null) {
                    existingResults.setFlutter(results.getFlutter());
                }
                if (results.getStudent_id() != null) {
                    existingResults.setStudent_id(results.getStudent_id());
                }
                if (results.getAvg() != null) {
                    existingResults.setAvg(results.getAvg());
                }
                if (results.getTotal_marks() != null) {
                    existingResults.setTotal_marks(results.getTotal_marks());
                }
                if (results.getResult() != null) {
                    existingResults.setResult(results.getResult());
                }

                return existingResults;
            })
            .flatMap(resultsRepository::save);
    }

    /**
     * Get all the results.
     *
     * @return the list of entities.
     */
    public Flux<Results> findAll() {
        log.debug("Request to get all Results");
        return resultsRepository.findAllWithEagerRelationships();
    }

    /**
     * Get all the results with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<Results> findAllWithEagerRelationships(Pageable pageable) {
        return resultsRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Returns the number of results available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return resultsRepository.count();
    }

    /**
     * Get one results by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<Results> findOne(String id) {
        log.debug("Request to get Results : {}", id);
        return resultsRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the results by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Results : {}", id);
        return resultsRepository.deleteById(id);
    }
}
