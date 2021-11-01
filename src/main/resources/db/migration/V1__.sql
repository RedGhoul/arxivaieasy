CREATE TABLE app_user
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    bio          LONGTEXT NULL,
    created_date datetime NULL,
    user_id      BIGINT NULL,
    CONSTRAINT pk_app_user PRIMARY KEY (id)
);

CREATE TABLE author
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    name         VARCHAR(255) NOT NULL,
    history_link VARCHAR(255) NOT NULL,
    CONSTRAINT pk_author PRIMARY KEY (id)
);

CREATE TABLE jhi_authority
(
    name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_jhi_authority PRIMARY KEY (name)
);

CREATE TABLE jhi_date_time_wrapper
(
    id               BIGINT NOT NULL,
    instant          datetime NULL,
    local_date_time  datetime NULL,
    offset_date_time datetime NULL,
    zoned_date_time  datetime NULL,
    local_time       datetime NULL,
    offset_time      time NULL,
    local_date       datetime NULL,
    CONSTRAINT pk_jhi_date_time_wrapper PRIMARY KEY (id)
);

CREATE TABLE jhi_user
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    created_by         VARCHAR(50) NOT NULL,
    created_date       datetime NULL,
    last_modified_by   VARCHAR(50) NULL,
    last_modified_date datetime NULL,
    login              VARCHAR(50) NOT NULL,
    password_hash      VARCHAR(60) NOT NULL,
    first_name         VARCHAR(50) NULL,
    last_name          VARCHAR(50) NULL,
    email              VARCHAR(254) NULL,
    activated          BIT(1)      NOT NULL,
    lang_key           VARCHAR(10) NULL,
    image_url          VARCHAR(256) NULL,
    activation_key     VARCHAR(20) NULL,
    reset_key          VARCHAR(20) NULL,
    reset_date         datetime NULL,
    CONSTRAINT pk_jhi_user PRIMARY KEY (id)
);

CREATE TABLE jhi_user_authority
(
    authority_name VARCHAR(50) NOT NULL,
    user_id        BIGINT      NOT NULL,
    CONSTRAINT pk_jhi_user_authority PRIMARY KEY (authority_name, user_id)
);

CREATE TABLE like_entry
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    create_date datetime NULL,
    CONSTRAINT pk_like_entry PRIMARY KEY (id)
);

CREATE TABLE paper
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    title         VARCHAR(255) NOT NULL,
    abstract_text LONGTEXT     NOT NULL,
    submited_date VARCHAR(255) NULL,
    created_date  datetime NULL,
    pdf_link      VARCHAR(255) NOT NULL,
    base_link     VARCHAR(255) NOT NULL,
    CONSTRAINT pk_paper PRIMARY KEY (id)
);

CREATE TABLE rel_like_entry__app_user
(
    app_user_id   BIGINT NOT NULL,
    like_entry_id BIGINT NOT NULL,
    CONSTRAINT pk_rel_like_entry__app_user PRIMARY KEY (app_user_id, like_entry_id)
);

CREATE TABLE rel_like_entry__paper
(
    like_entry_id BIGINT NOT NULL,
    paper_id      BIGINT NOT NULL,
    CONSTRAINT pk_rel_like_entry__paper PRIMARY KEY (like_entry_id, paper_id)
);

CREATE TABLE rel_paper__author
(
    author_id BIGINT NOT NULL,
    paper_id  BIGINT NOT NULL,
    CONSTRAINT pk_rel_paper__author PRIMARY KEY (author_id, paper_id)
);

CREATE TABLE rel_paper__subject
(
    paper_id   BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    CONSTRAINT pk_rel_paper__subject PRIMARY KEY (paper_id, subject_id)
);

CREATE TABLE subject
(
    id    BIGINT AUTO_INCREMENT NOT NULL,
    title VARCHAR(255) NOT NULL,
    CONSTRAINT pk_subject PRIMARY KEY (id)
);

ALTER TABLE app_user
    ADD CONSTRAINT uc_app_user_user UNIQUE (user_id);

ALTER TABLE jhi_user
    ADD CONSTRAINT uc_jhi_user_email UNIQUE (email);

ALTER TABLE jhi_user
    ADD CONSTRAINT uc_jhi_user_login UNIQUE (login);

ALTER TABLE paper
    ADD CONSTRAINT uc_paper_base_link UNIQUE (base_link);

ALTER TABLE app_user
    ADD CONSTRAINT FK_APP_USER_ON_USER FOREIGN KEY (user_id) REFERENCES jhi_user (id);

ALTER TABLE jhi_user_authority
    ADD CONSTRAINT fk_jhiuseaut_on_authority FOREIGN KEY (authority_name) REFERENCES jhi_authority (name);

ALTER TABLE jhi_user_authority
    ADD CONSTRAINT fk_jhiuseaut_on_user FOREIGN KEY (user_id) REFERENCES jhi_user (id);

ALTER TABLE rel_like_entry__app_user
    ADD CONSTRAINT fk_rellikentappuse_on_app_user FOREIGN KEY (app_user_id) REFERENCES app_user (id);

ALTER TABLE rel_like_entry__app_user
    ADD CONSTRAINT fk_rellikentappuse_on_like_entry FOREIGN KEY (like_entry_id) REFERENCES like_entry (id);

ALTER TABLE rel_like_entry__paper
    ADD CONSTRAINT fk_rellikentpap_on_like_entry FOREIGN KEY (like_entry_id) REFERENCES like_entry (id);

ALTER TABLE rel_like_entry__paper
    ADD CONSTRAINT fk_rellikentpap_on_paper FOREIGN KEY (paper_id) REFERENCES paper (id);

ALTER TABLE rel_paper__author
    ADD CONSTRAINT fk_relpapaut_on_author FOREIGN KEY (author_id) REFERENCES author (id);

ALTER TABLE rel_paper__author
    ADD CONSTRAINT fk_relpapaut_on_paper FOREIGN KEY (paper_id) REFERENCES paper (id);

ALTER TABLE rel_paper__subject
    ADD CONSTRAINT fk_relpapsub_on_paper FOREIGN KEY (paper_id) REFERENCES paper (id);

ALTER TABLE rel_paper__subject
    ADD CONSTRAINT fk_relpapsub_on_subject FOREIGN KEY (subject_id) REFERENCES subject (id);
