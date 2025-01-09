package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResultsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Results.class);
        Results results1 = new Results();
        results1.setId("id1");
        Results results2 = new Results();
        results2.setId(results1.getId());
        assertThat(results1).isEqualTo(results2);
        results2.setId("id2");
        assertThat(results1).isNotEqualTo(results2);
        results1.setId(null);
        assertThat(results1).isNotEqualTo(results2);
    }
}
