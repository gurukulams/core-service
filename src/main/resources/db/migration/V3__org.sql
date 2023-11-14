CREATE TABLE org (
    user_handle VARCHAR(40) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(800) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(40) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(40),
    FOREIGN KEY (user_handle) REFERENCES handle (user_handle),
    CONSTRAINT org_title_constraint UNIQUE (title)
);

CREATE TABLE org_localized (
    user_handle VARCHAR(40),
    locale VARCHAR(8) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(800) NOT NULL,
    FOREIGN KEY (user_handle) REFERENCES org (user_handle),
    PRIMARY KEY(user_handle, locale)
);

CREATE TABLE org_learner (
    org_handle VARCHAR(40) NOT NULL,
    learner_handle VARCHAR(40) NOT NULL,
    PRIMARY KEY(org_handle, learner_handle),
    FOREIGN KEY (org_handle) REFERENCES org (user_handle),
    FOREIGN KEY (learner_handle) REFERENCES learner (user_handle)
);