CREATE TABLE handle (
    user_handle VARCHAR(200) PRIMARY KEY,
    type VARCHAR(55),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT type_id_constraint UNIQUE (type, user_handle)
);

CREATE TABLE learner (
    user_handle VARCHAR(200) PRIMARY KEY,
    email VARCHAR(200) NOT NULL,
    image_url VARCHAR(200) NOT NULL,
    provider VARCHAR(50) DEFAULT 'local' NOT NULL,
    pword VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT learner_email_constraint UNIQUE (email),
    FOREIGN KEY (user_handle) REFERENCES handle (user_handle)
);

CREATE TABLE learner_profile (
    user_handle VARCHAR(200) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    dob DATE NOT NULL,
    FOREIGN KEY (user_handle) REFERENCES handle (user_handle)
);

CREATE TABLE org (
    user_handle VARCHAR(200) PRIMARY KEY,
    title TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200),
    FOREIGN KEY (user_handle) REFERENCES handle (user_handle),
    CONSTRAINT org_title_constraint UNIQUE (title)
);

CREATE TABLE category (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(200),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200)
);

CREATE TABLE category_localized (
    category_id VARCHAR(50),
    locale VARCHAR(8) NOT NULL,
    title VARCHAR(200),
    description TEXT,
    FOREIGN KEY (category_id) REFERENCES category (id),
    PRIMARY KEY(category_id, locale)
);

CREATE TABLE tag (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(200),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200)
);

CREATE TABLE tag_localized (
    tag_id VARCHAR(50),
    locale VARCHAR(8) NOT NULL,
    title VARCHAR(200),
    description TEXT,
    FOREIGN KEY (tag_id) REFERENCES tag (id),
    PRIMARY KEY(tag_id, locale)
);