package com.ava.arxivai.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ava.arxivai.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LikeEntryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LikeEntry.class);
        LikeEntry likeEntry1 = new LikeEntry();
        likeEntry1.setId(1L);
        LikeEntry likeEntry2 = new LikeEntry();
        likeEntry2.setId(likeEntry1.getId());
        assertThat(likeEntry1).isEqualTo(likeEntry2);
        likeEntry2.setId(2L);
        assertThat(likeEntry1).isNotEqualTo(likeEntry2);
        likeEntry1.setId(null);
        assertThat(likeEntry1).isNotEqualTo(likeEntry2);
    }
}
