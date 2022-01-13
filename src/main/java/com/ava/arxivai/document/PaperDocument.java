package com.ava.arxivai.document;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;

@Document(indexName = "paper_arxixai")
public class PaperDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String abstractText;

    @Field(type = FieldType.Integer)
    private Integer submitedDate;

    @Field(type = FieldType.Integer)
    private Integer createdDate;
}
