package com.ava.arxivai.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ava.arxivai.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaperTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Paper.class);
        Paper paper1 = new Paper();
        paper1.setId(1L);
        Paper paper2 = new Paper();
        paper2.setId(paper1.getId());
        assertThat(paper1).isEqualTo(paper2);
        paper2.setId(2L);
        assertThat(paper1).isNotEqualTo(paper2);
        paper1.setId(null);
        assertThat(paper1).isNotEqualTo(paper2);
    }
}
