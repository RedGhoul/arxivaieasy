package com.ava.arxivai.repository;

import com.ava.arxivai.document.PaperDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaperIndexRepository extends ElasticsearchRepository<PaperDocument, String> {
}
