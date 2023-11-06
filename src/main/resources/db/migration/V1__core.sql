CREATE TABLE handle (
    user_handle VARCHAR(40) PRIMARY KEY,
    type VARCHAR(40),
    CONSTRAINT type_id_constraint UNIQUE (type, user_handle)
);

CREATE TABLE learner (
    user_handle VARCHAR(40) PRIMARY KEY,
    email VARCHAR(200) NOT NULL,
    image_url VARCHAR(200) NOT NULL,
    provider VARCHAR(50) DEFAULT 'local' NOT NULL,
    pword VARCHAR(200),
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP ,
    CONSTRAINT learner_email_constraint UNIQUE (email),
    FOREIGN KEY (user_handle) REFERENCES handle (user_handle)
);

CREATE TABLE learner_profile (
    user_handle VARCHAR(40) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    dob DATE NOT NULL,
    FOREIGN KEY (user_handle) REFERENCES handle (user_handle)
);

CREATE TABLE org (
    user_handle VARCHAR(40) PRIMARY KEY,
    title VARCHAR(800),
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(40) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(40),
    FOREIGN KEY (user_handle) REFERENCES handle (user_handle),
    CONSTRAINT org_title_constraint UNIQUE (title)
);

CREATE TABLE category (
    id VARCHAR(40) PRIMARY KEY,
    title VARCHAR(200),
    description VARCHAR(800),
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(40) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(40)
);

CREATE TABLE category_localized (
    category_id VARCHAR(40),
    locale VARCHAR(8) NOT NULL,
    title VARCHAR(200),
    description VARCHAR(800),
    FOREIGN KEY (category_id) REFERENCES category (id),
    PRIMARY KEY(category_id, locale)
);

CREATE TABLE tag (
    id VARCHAR(40) PRIMARY KEY,
    title VARCHAR(200),
    description VARCHAR(800),
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(40) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(40)
);

CREATE TABLE tag_localized (
    tag_id VARCHAR(40),
    locale VARCHAR(8) NOT NULL,
    title VARCHAR(200),
    description VARCHAR(800),
    FOREIGN KEY (tag_id) REFERENCES tag (id),
    PRIMARY KEY(tag_id, locale)
);