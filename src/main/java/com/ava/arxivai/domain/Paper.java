package com.ava.arxivai.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.mapstruct.Builder;

/**
 * A Paper.
 */
@Entity
@Table(name = "paper")
public class Paper implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "abstract_text", nullable = false)
    private String abstractText;

    @Column(name = "submited_date")
    private String submitedDate;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @NotNull
    @Column(name = "pdf_link", nullable = false)
    private String pdfLink;

    @NotNull
    @Column(name = "base_link", nullable = false, unique = true)
    private String baseLink;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "rel_paper__author",
        joinColumns = @JoinColumn(name = "paper_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @JsonIgnoreProperties(value = { "papers" }, allowSetters = true)
    private Set<Author> authors = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "rel_paper__subject",
        joinColumns = @JoinColumn(name = "paper_id"),
        inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    @JsonIgnoreProperties(value = { "papers" }, allowSetters = true)
    private Set<Subject> subjects = new HashSet<>();

    @ManyToMany(mappedBy = "papers", fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "appUsers", "papers" }, allowSetters = true)
    private Set<LikeEntry> likeEntries = new HashSet<>();

    public void setSubjects(Set<Subject> Subjects) {
        this.subjects = Subjects;
    }

    public Set<Subject> getSubjects() {
        return this.subjects;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paper id(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public Paper title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstractText() {
        return this.abstractText;
    }

    public Paper abstractText(String abstractText) {
        this.abstractText = abstractText;
        return this;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getSubmitedDate() {
        return this.submitedDate;
    }

    public Paper submitedDate(String submitedDate) {
        this.submitedDate = submitedDate;
        return this;
    }

    public void setSubmitedDate(String submitedDate) {
        this.submitedDate = submitedDate;
    }

    public LocalDate getCreatedDate() {
        return this.createdDate;
    }

    public Paper createdDate(LocalDate createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public String getPdfLink() {
        return this.pdfLink;
    }

    public Paper pdfLink(String pdfLink) {
        this.pdfLink = pdfLink;
        return this;
    }

    public void setPdfLink(String pdfLink) {
        this.pdfLink = pdfLink;
    }

    public String getBaseLink() {
        return this.baseLink;
    }

    public Paper baseLink(String baseLink) {
        this.baseLink = baseLink;
        return this;
    }

    public void setBaseLink(String baseLink) {
        this.baseLink = baseLink;
    }

    public Set<Author> getAuthors() {
        return this.authors;
    }

    public Paper authors(Set<Author> authors) {
        this.setAuthors(authors);
        return this;
    }

    public Paper addAuthor(Author author) {
        this.authors.add(author);
        author.getPapers().add(this);
        return this;
    }

    public Paper removeAuthor(Author author) {
        this.authors.remove(author);
        author.getPapers().remove(this);
        return this;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public Set<LikeEntry> getLikeEntries() {
        return this.likeEntries;
    }

    public Paper likeEntries(Set<LikeEntry> likeEntries) {
        this.setLikeEntries(likeEntries);
        return this;
    }

    public Paper addLikeEntry(LikeEntry likeEntry) {
        this.likeEntries.add(likeEntry);
        likeEntry.getPapers().add(this);
        return this;
    }

    public Paper removeLikeEntry(LikeEntry likeEntry) {
        this.likeEntries.remove(likeEntry);
        likeEntry.getPapers().remove(this);
        return this;
    }

    public void setLikeEntries(Set<LikeEntry> likeEntries) {
        if (this.likeEntries != null) {
            this.likeEntries.forEach(i -> i.removePaper(this));
        }
        if (likeEntries != null) {
            likeEntries.forEach(i -> i.addPaper(this));
        }
        this.likeEntries = likeEntries;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Paper)) {
            return false;
        }
        return id != null && id.equals(((Paper) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Paper{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", abstractText='" + getAbstractText() + "'" +
            ", submitedDate='" + getSubmitedDate() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", pdfLink='" + getPdfLink() + "'" +
            ", baseLink='" + getBaseLink() + "'" +
            "}";
    }
}
