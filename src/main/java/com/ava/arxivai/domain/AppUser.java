package com.ava.arxivai.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * A AppUser.
 */
@Entity
@Table(name = "app_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AppUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "bio")
    private String bio;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @ManyToMany(mappedBy = "appUsers")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "appUsers", "papers" }, allowSetters = true)
    private Set<LikeEntry> likeEntries = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppUser id(Long id) {
        this.id = id;
        return this;
    }

    public String getBio() {
        return this.bio;
    }

    public AppUser bio(String bio) {
        this.bio = bio;
        return this;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public LocalDate getCreatedDate() {
        return this.createdDate;
    }

    public AppUser createdDate(LocalDate createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public User getUser() {
        return this.user;
    }

    public AppUser user(User user) {
        this.setUser(user);
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<LikeEntry> getLikeEntries() {
        return this.likeEntries;
    }

    public AppUser likeEntries(Set<LikeEntry> likeEntries) {
        this.setLikeEntries(likeEntries);
        return this;
    }

    public AppUser addLikeEntry(LikeEntry likeEntry) {
        this.likeEntries.add(likeEntry);
        likeEntry.getAppUsers().add(this);
        return this;
    }

    public AppUser removeLikeEntry(LikeEntry likeEntry) {
        this.likeEntries.remove(likeEntry);
        likeEntry.getAppUsers().remove(this);
        return this;
    }

    public void setLikeEntries(Set<LikeEntry> likeEntries) {
        if (this.likeEntries != null) {
            this.likeEntries.forEach(i -> i.removeAppUser(this));
        }
        if (likeEntries != null) {
            likeEntries.forEach(i -> i.addAppUser(this));
        }
        this.likeEntries = likeEntries;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUser)) {
            return false;
        }
        return id != null && id.equals(((AppUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUser{" +
            "id=" + getId() +
            ", bio='" + getBio() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            "}";
    }
}
