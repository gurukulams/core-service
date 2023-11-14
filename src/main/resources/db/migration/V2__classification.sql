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