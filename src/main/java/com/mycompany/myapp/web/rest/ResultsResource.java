package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Results;
import com.mycompany.myapp.repository.ResultsRepository;
import com.mycompany.myapp.service.ResultsService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Results}.
 */
@RestController
@RequestMapping("/api")
public class ResultsResource {

    private final Logger log = LoggerFactory.getLogger(ResultsResource.class);

    private static final String ENTITY_NAME = "results";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ResultsService resultsService;

    private final ResultsRepository resultsRepository;

    public ResultsResource(ResultsService resultsService, ResultsRepository resultsRepository) {
        this.resultsService = resultsService;
        this.resultsRepository = resultsRepository;
    }

    /**
     * {@code POST  /results} : Create a new results.
     *
     * @param results the results to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new results, or with status {@code 400 (Bad Request)} if the results has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/results")
    public Mono<ResponseEntity<Results>> createResults(@RequestBody Results results) throws URISyntaxException {
        log.debug("REST request to save Results : {}", results);
        if (results.getId() != null) {
            throw new BadRequestAlertException("A new results cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return resultsService
            .save(results)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/results/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /results/:id} : Updates an existing results.
     *
     * @param id the id of the results to save.
     * @param results the results to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated results,
     * or with status {@code 400 (Bad Request)} if the results is not valid,
     * or with status {@code 500 (Internal Server Error)} if the results couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/results/{id}")
    public Mono<ResponseEntity<Results>> updateResults(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Results results
    ) throws URISyntaxException {
        log.debug("REST request to update Results : {}, {}", id, results);
        if (results.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, results.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return resultsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return resultsService
                    .update(results)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /results/:id} : Partial updates given fields of an existing results, field will ignore if it is null
     *
     * @param id the id of the results to save.
     * @param results the results to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated results,
     * or with status {@code 400 (Bad Request)} if the results is not valid,
     * or with status {@code 404 (Not Found)} if the results is not found,
     * or with status {@code 500 (Internal Server Error)} if the results couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/results/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Results>> partialUpdateResults(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Results results
    ) throws URISyntaxException {
        log.debug("REST request to partial update Results partially : {}, {}", id, results);
        if (results.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, results.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return resultsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Results> result = resultsService.partialUpdate(results);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /results} : get all the results.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of results in body.
     */
    @GetMapping("/results")
    public Mono<List<Results>> getAllResults(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Results");
        return resultsService.findAll().collectList();
    }

    /**
     * {@code GET  /results} : get all the results as a stream.
     * @return the {@link Flux} of results.
     */
    @GetMapping(value = "/results", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Results> getAllResultsAsStream() {
        log.debug("REST request to get all Results as a stream");
        return resultsService.findAll();
    }

    /**
     * {@code GET  /results/:id} : get the "id" results.
     *
     * @param id the id of the results to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the results, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/results/{id}")
    public Mono<ResponseEntity<Results>> getResults(@PathVariable String id) {
        log.debug("REST request to get Results : {}", id);
        Mono<Results> results = resultsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(results);
    }

    /**
     * {@code DELETE  /results/:id} : delete the "id" results.
     *
     * @param id the id of the results to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/results/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteResults(@PathVariable String id) {
        log.debug("REST request to delete Results : {}", id);
        return resultsService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build()
            );
    }
}
