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
    created_at TIMESTAMP,
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

CREATE TABLE learner_buddy (
    buddy_handle VARCHAR(40) NOT NULL,
    learner_handle VARCHAR(40) NOT NULL,
    PRIMARY KEY(buddy_handle, learner_handle),
    FOREIGN KEY (buddy_handle) REFERENCES learner (user_handle),
    FOREIGN KEY (learner_handle) REFERENCES learner (user_handle)
);