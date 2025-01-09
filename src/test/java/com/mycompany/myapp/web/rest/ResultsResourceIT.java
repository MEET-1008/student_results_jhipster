package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Results;
import com.mycompany.myapp.domain.enumeration.pass_faill;
import com.mycompany.myapp.repository.ResultsRepository;
import com.mycompany.myapp.service.ResultsService;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link ResultsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ResultsResourceIT {

    private static final String DEFAULT_JAVA = "AAAAAAAAAA";
    private static final String UPDATED_JAVA = "BBBBBBBBBB";

    private static final String DEFAULT_PYTHON = "AAAAAAAAAA";
    private static final String UPDATED_PYTHON = "BBBBBBBBBB";

    private static final String DEFAULT_FLUTTER = "AAAAAAAAAA";
    private static final String UPDATED_FLUTTER = "BBBBBBBBBB";

    private static final String DEFAULT_STUDENT_ID = "AAAAAAAAAA";
    private static final String UPDATED_STUDENT_ID = "BBBBBBBBBB";

    private static final Double DEFAULT_AVG = 1D;
    private static final Double UPDATED_AVG = 2D;

    private static final Double DEFAULT_TOTAL_MARKS = 1D;
    private static final Double UPDATED_TOTAL_MARKS = 2D;

    private static final pass_faill DEFAULT_RESULT = pass_faill.PASS;
    private static final pass_faill UPDATED_RESULT = pass_faill.FAIL;

    private static final String ENTITY_API_URL = "/api/results";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ResultsRepository resultsRepository;

    @Mock
    private ResultsRepository resultsRepositoryMock;

    @Mock
    private ResultsService resultsServiceMock;

    @Autowired
    private WebTestClient webTestClient;

    private Results results;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Results createEntity() {
        Results results = new Results()
            .java(DEFAULT_JAVA)
            .python(DEFAULT_PYTHON)
            .flutter(DEFAULT_FLUTTER)
            .student_id(DEFAULT_STUDENT_ID)
            .avg(DEFAULT_AVG)
            .total_marks(DEFAULT_TOTAL_MARKS)
            .result(DEFAULT_RESULT);
        return results;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Results createUpdatedEntity() {
        Results results = new Results()
            .java(UPDATED_JAVA)
            .python(UPDATED_PYTHON)
            .flutter(UPDATED_FLUTTER)
            .student_id(UPDATED_STUDENT_ID)
            .avg(UPDATED_AVG)
            .total_marks(UPDATED_TOTAL_MARKS)
            .result(UPDATED_RESULT);
        return results;
    }

    @BeforeEach
    public void initTest() {
        resultsRepository.deleteAll().block();
        results = createEntity();
    }

    @Test
    void createResults() throws Exception {
        int databaseSizeBeforeCreate = resultsRepository.findAll().collectList().block().size();
        // Create the Results
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(results))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Results in the database
        List<Results> resultsList = resultsRepository.findAll().collectList().block();
        assertThat(resultsList).hasSize(databaseSizeBeforeCreate + 1);
        Results testResults = resultsList.get(resultsList.size() - 1);
        assertThat(testResults.getJava()).isEqualTo(DEFAULT_JAVA);
        assertThat(testResults.getPython()).isEqualTo(DEFAULT_PYTHON);
        assertThat(testResults.getFlutter()).isEqualTo(DEFAULT_FLUTTER);
        assertThat(testResults.getStudent_id()).isEqualTo(DEFAULT_STUDENT_ID);
        assertThat(testResults.getAvg()).isEqualTo(DEFAULT_AVG);
        assertThat(testResults.getTotal_marks()).isEqualTo(DEFAULT_TOTAL_MARKS);
        assertThat(testResults.getResult()).isEqualTo(DEFAULT_RESULT);
    }

    @Test
    void createResultsWithExistingId() throws Exception {
        // Create the Results with an existing ID
        results.setId("existing_id");

        int databaseSizeBeforeCreate = resultsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(results))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Results in the database
        List<Results> resultsList = resultsRepository.findAll().collectList().block();
        assertThat(resultsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllResultsAsStream() {
        // Initialize the database
        resultsRepository.save(results).block();

        List<Results> resultsList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Results.class)
            .getResponseBody()
            .filter(results::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(resultsList).isNotNull();
        assertThat(resultsList).hasSize(1);
        Results testResults = resultsList.get(0);
        assertThat(testResults.getJava()).isEqualTo(DEFAULT_JAVA);
        assertThat(testResults.getPython()).isEqualTo(DEFAULT_PYTHON);
        assertThat(testResults.getFlutter()).isEqualTo(DEFAULT_FLUTTER);
        assertThat(testResults.getStudent_id()).isEqualTo(DEFAULT_STUDENT_ID);
        assertThat(testResults.getAvg()).isEqualTo(DEFAULT_AVG);
        assertThat(testResults.getTotal_marks()).isEqualTo(DEFAULT_TOTAL_MARKS);
        assertThat(testResults.getResult()).isEqualTo(DEFAULT_RESULT);
    }

    @Test
    void getAllResults() {
        // Initialize the database
        resultsRepository.save(results).block();

        // Get all the resultsList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(results.getId()))
            .jsonPath("$.[*].java")
            .value(hasItem(DEFAULT_JAVA))
            .jsonPath("$.[*].python")
            .value(hasItem(DEFAULT_PYTHON))
            .jsonPath("$.[*].flutter")
            .value(hasItem(DEFAULT_FLUTTER))
            .jsonPath("$.[*].student_id")
            .value(hasItem(DEFAULT_STUDENT_ID))
            .jsonPath("$.[*].avg")
            .value(hasItem(DEFAULT_AVG.doubleValue()))
            .jsonPath("$.[*].total_marks")
            .value(hasItem(DEFAULT_TOTAL_MARKS.doubleValue()))
            .jsonPath("$.[*].result")
            .value(hasItem(DEFAULT_RESULT.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllResultsWithEagerRelationshipsIsEnabled() {
        when(resultsServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(resultsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllResultsWithEagerRelationshipsIsNotEnabled() {
        when(resultsServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(resultsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getResults() {
        // Initialize the database
        resultsRepository.save(results).block();

        // Get the results
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, results.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(results.getId()))
            .jsonPath("$.java")
            .value(is(DEFAULT_JAVA))
            .jsonPath("$.python")
            .value(is(DEFAULT_PYTHON))
            .jsonPath("$.flutter")
            .value(is(DEFAULT_FLUTTER))
            .jsonPath("$.student_id")
            .value(is(DEFAULT_STUDENT_ID))
            .jsonPath("$.avg")
            .value(is(DEFAULT_AVG.doubleValue()))
            .jsonPath("$.total_marks")
            .value(is(DEFAULT_TOTAL_MARKS.doubleValue()))
            .jsonPath("$.result")
            .value(is(DEFAULT_RESULT.toString()));
    }

    @Test
    void getNonExistingResults() {
        // Get the results
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewResults() throws Exception {
        // Initialize the database
        resultsRepository.save(results).block();

        int databaseSizeBeforeUpdate = resultsRepository.findAll().collectList().block().size();

        // Update the results
        Results updatedResults = resultsRepository.findById(results.getId()).block();
        updatedResults
            .java(UPDATED_JAVA)
            .python(UPDATED_PYTHON)
            .flutter(UPDATED_FLUTTER)
            .student_id(UPDATED_STUDENT_ID)
            .avg(UPDATED_AVG)
            .total_marks(UPDATED_TOTAL_MARKS)
            .result(UPDATED_RESULT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedResults.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedResults))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Results in the database
        List<Results> resultsList = resultsRepository.findAll().collectList().block();
        assertThat(resultsList).hasSize(databaseSizeBeforeUpdate);
        Results testResults = resultsList.get(resultsList.size() - 1);
        assertThat(testResults.getJava()).isEqualTo(UPDATED_JAVA);
        assertThat(testResults.getPython()).isEqualTo(UPDATED_PYTHON);
        assertThat(testResults.getFlutter()).isEqualTo(UPDATED_FLUTTER);
        assertThat(testResults.getStudent_id()).isEqualTo(UPDATED_STUDENT_ID);
        assertThat(testResults.getAvg()).isEqualTo(UPDATED_AVG);
        assertThat(testResults.getTotal_marks()).isEqualTo(UPDATED_TOTAL_MARKS);
        assertThat(testResults.getResult()).isEqualTo(UPDATED_RESULT);
    }

    @Test
    void putNonExistingResults() throws Exception {
        int databaseSizeBeforeUpdate = resultsRepository.findAll().collectList().block().size();
        results.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, results.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(results))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Results in the database
        List<Results> resultsList = resultsRepository.findAll().collectList().block();
        assertThat(resultsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchResults() throws Exception {
        int databaseSizeBeforeUpdate = resultsRepository.findAll().collectList().block().size();
        results.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(results))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Results in the database
        List<Results> resultsList = resultsRepository.findAll().collectList().block();
        assertThat(resultsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamResults() throws Exception {
        int databaseSizeBeforeUpdate = resultsRepository.findAll().collectList().block().size();
        results.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(results))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Results in the database
        List<Results> resultsList = resultsRepository.findAll().collectList().block();
        assertThat(resultsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateResultsWithPatch() throws Exception {
        // Initialize the database
        resultsRepository.save(results).block();

        int databaseSizeBeforeUpdate = resultsRepository.findAll().collectList().block().size();

        // Update the results using partial update
        Results partialUpdatedResults = new Results();
        partialUpdatedResults.setId(results.getId());

        partialUpdatedResults.student_id(UPDATED_STUDENT_ID).avg(UPDATED_AVG);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedResults.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedResults))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Results in the database
        List<Results> resultsList = resultsRepository.findAll().collectList().block();
        assertThat(resultsList).hasSize(databaseSizeBeforeUpdate);
        Results testResults = resultsList.get(resultsList.size() - 1);
        assertThat(testResults.getJava()).isEqualTo(DEFAULT_JAVA);
        assertThat(testResults.getPython()).isEqualTo(DEFAULT_PYTHON);
        assertThat(testResults.getFlutter()).isEqualTo(DEFAULT_FLUTTER);
        assertThat(testResults.getStudent_id()).isEqualTo(UPDATED_STUDENT_ID);
        assertThat(testResults.getAvg()).isEqualTo(UPDATED_AVG);
        assertThat(testResults.getTotal_marks()).isEqualTo(DEFAULT_TOTAL_MARKS);
        assertThat(testResults.getResult()).isEqualTo(DEFAULT_RESULT);
    }

    @Test
    void fullUpdateResultsWithPatch() throws Exception {
        // Initialize the database
        resultsRepository.save(results).block();

        int databaseSizeBeforeUpdate = resultsRepository.findAll().collectList().block().size();

        // Update the results using partial update
        Results partialUpdatedResults = new Results();
        partialUpdatedResults.setId(results.getId());

        partialUpdatedResults
            .java(UPDATED_JAVA)
            .python(UPDATED_PYTHON)
            .flutter(UPDATED_FLUTTER)
            .student_id(UPDATED_STUDENT_ID)
            .avg(UPDATED_AVG)
            .total_marks(UPDATED_TOTAL_MARKS)
            .result(UPDATED_RESULT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedResults.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedResults))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Results in the database
        List<Results> resultsList = resultsRepository.findAll().collectList().block();
        assertThat(resultsList).hasSize(databaseSizeBeforeUpdate);
        Results testResults = resultsList.get(resultsList.size() - 1);
        assertThat(testResults.getJava()).isEqualTo(UPDATED_JAVA);
        assertThat(testResults.getPython()).isEqualTo(UPDATED_PYTHON);
        assertThat(testResults.getFlutter()).isEqualTo(UPDATED_FLUTTER);
        assertThat(testResults.getStudent_id()).isEqualTo(UPDATED_STUDENT_ID);
        assertThat(testResults.getAvg()).isEqualTo(UPDATED_AVG);
        assertThat(testResults.getTotal_marks()).isEqualTo(UPDATED_TOTAL_MARKS);
        assertThat(testResults.getResult()).isEqualTo(UPDATED_RESULT);
    }

    @Test
    void patchNonExistingResults() throws Exception {
        int databaseSizeBeforeUpdate = resultsRepository.findAll().collectList().block().size();
        results.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, results.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(results))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Results in the database
        List<Results> resultsList = resultsRepository.findAll().collectList().block();
        assertThat(resultsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchResults() throws Exception {
        int databaseSizeBeforeUpdate = resultsRepository.findAll().collectList().block().size();
        results.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(results))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Results in the database
        List<Results> resultsList = resultsRepository.findAll().collectList().block();
        assertThat(resultsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamResults() throws Exception {
        int databaseSizeBeforeUpdate = resultsRepository.findAll().collectList().block().size();
        results.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(results))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Results in the database
        List<Results> resultsList = resultsRepository.findAll().collectList().block();
        assertThat(resultsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteResults() {
        // Initialize the database
        resultsRepository.save(results).block();

        int databaseSizeBeforeDelete = resultsRepository.findAll().collectList().block().size();

        // Delete the results
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, results.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Results> resultsList = resultsRepository.findAll().collectList().block();
        assertThat(resultsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
