package com.ava.arxivai.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Author.
 */
@Entity
@Table(name = "author")
public class Author implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "history_link", nullable = false)
    private String historyLink;

    @ManyToMany(mappedBy = "authors")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "authors", "likeEntries" }, allowSetters = true)
    private Set<Paper> papers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Author id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Author name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHistoryLink() {
        return this.historyLink;
    }

    public Author historyLink(String historyLink) {
        this.historyLink = historyLink;
        return this;
    }

    public void setHistoryLink(String historyLink) {
        this.historyLink = historyLink;
    }

    public Set<Paper> getPapers() {
        return this.papers;
    }

    public Author papers(Set<Paper> papers) {
        this.setPapers(papers);
        return this;
    }

    public Author addPaper(Paper paper) {
        this.papers.add(paper);
        paper.getAuthors().add(this);
        return this;
    }

    public Author removePaper(Paper paper) {
        this.papers.remove(paper);
        paper.getAuthors().remove(this);
        return this;
    }

    public void setPapers(Set<Paper> papers) {
        if (this.papers != null) {
            this.papers.forEach(i -> i.removeAuthor(this));
        }
        if (papers != null) {
            papers.forEach(i -> i.addAuthor(this));
        }
        this.papers = papers;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Author)) {
            return false;
        }
        return id != null && id.equals(((Author) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Author{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", historyLink='" + getHistoryLink() + "'" +
            "}";
    }
}
