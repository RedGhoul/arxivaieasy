package com.ava.arxivai.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LikeEntry.
 */
@Entity
@Table(name = "like_entry")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LikeEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "create_date")
    private LocalDate createDate;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(
        name = "rel_like_entry__app_user",
        joinColumns = @JoinColumn(name = "like_entry_id"),
        inverseJoinColumns = @JoinColumn(name = "app_user_id")
    )
    @JsonIgnoreProperties(value = { "user", "likeEntries" }, allowSetters = true)
    private Set<AppUser> appUsers = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(
        name = "rel_like_entry__paper",
        joinColumns = @JoinColumn(name = "like_entry_id"),
        inverseJoinColumns = @JoinColumn(name = "paper_id")
    )
    @JsonIgnoreProperties(value = { "authors", "likeEntries" }, allowSetters = true)
    private Set<Paper> papers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LikeEntry id(Long id) {
        this.id = id;
        return this;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public LikeEntry createDate(LocalDate createDate) {
        this.createDate = createDate;
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public Set<AppUser> getAppUsers() {
        return this.appUsers;
    }

    public LikeEntry appUsers(Set<AppUser> appUsers) {
        this.setAppUsers(appUsers);
        return this;
    }

    public LikeEntry addAppUser(AppUser appUser) {
        this.appUsers.add(appUser);
        appUser.getLikeEntries().add(this);
        return this;
    }

    public LikeEntry removeAppUser(AppUser appUser) {
        this.appUsers.remove(appUser);
        appUser.getLikeEntries().remove(this);
        return this;
    }

    public void setAppUsers(Set<AppUser> appUsers) {
        this.appUsers = appUsers;
    }

    public Set<Paper> getPapers() {
        return this.papers;
    }

    public LikeEntry papers(Set<Paper> papers) {
        this.setPapers(papers);
        return this;
    }

    public LikeEntry addPaper(Paper paper) {
        this.papers.add(paper);
        paper.getLikeEntries().add(this);
        return this;
    }

    public LikeEntry removePaper(Paper paper) {
        this.papers.remove(paper);
        paper.getLikeEntries().remove(this);
        return this;
    }

    public void setPapers(Set<Paper> papers) {
        this.papers = papers;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LikeEntry)) {
            return false;
        }
        return id != null && id.equals(((LikeEntry) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LikeEntry{" +
            "id=" + getId() +
            ", createDate='" + getCreateDate() + "'" +
            "}";
    }
}
