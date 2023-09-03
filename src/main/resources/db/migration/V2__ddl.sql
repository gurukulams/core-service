CREATE TABLE annotations (
   id UUID PRIMARY KEY,
   on_type VARCHAR NOT NULL,
   on_instance VARCHAR NOT NULL,
   locale VARCHAR(8),
   json_value JSON NOT NULL,
   created_by VARCHAR(55) NOT NULL
);

CREATE TABLE boards (
    id UUID PRIMARY KEY,
    title VARCHAR(55),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200),
    CONSTRAINT boards_title_constraint UNIQUE (title)
);

CREATE TABLE boards_localized (
    board_id UUID,
    locale VARCHAR(8) NOT NULL,
    title VARCHAR(55),
    description TEXT,
    FOREIGN KEY (board_id) REFERENCES boards (id),
    PRIMARY KEY(board_id, locale)
);

CREATE TABLE grades (
    id UUID PRIMARY KEY,
    title VARCHAR(55),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200),
    CONSTRAINT grades_title_constraint UNIQUE (title)
);

CREATE TABLE grades_localized (
    grade_id UUID,
    locale VARCHAR(8) NOT NULL,
    title VARCHAR(55),
    description TEXT,
    FOREIGN KEY (grade_id) REFERENCES grades (id),
    PRIMARY KEY(grade_id, locale)
);

CREATE TABLE subjects (
    id UUID PRIMARY KEY,
    title VARCHAR(55),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200),
    CONSTRAINT subjects_title_constraint UNIQUE (title)
);

CREATE TABLE subjects_localized (
    subject_id UUID,
    locale VARCHAR(8) NOT NULL,
    title VARCHAR(55),
    description TEXT,
    FOREIGN KEY (subject_id) REFERENCES subjects (id),
    PRIMARY KEY(subject_id, locale)
);

CREATE TABLE books (
    id UUID PRIMARY KEY,
    title VARCHAR(55),
    path VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200),
    CONSTRAINT books_path_constraint UNIQUE (path),
    CONSTRAINT books_title_constraint UNIQUE (title)
);

CREATE TABLE books_localized (
    book_id UUID,
    locale VARCHAR(8) NOT NULL,
    title VARCHAR(55),
    description TEXT,
    FOREIGN KEY (book_id) REFERENCES books (id),
    PRIMARY KEY(book_id, locale)
);


CREATE TABLE institutes (
    id VARCHAR(200) PRIMARY KEY,
    title VARCHAR(55),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200),
    CONSTRAINT institutes_title_constraint UNIQUE (title),
    FOREIGN KEY (id) REFERENCES handle (user_handle)
);

CREATE TABLE institutes_localized (
    institute_id VARCHAR(55),
    locale VARCHAR(8) NOT NULL,
    title VARCHAR(55),
    description TEXT,
    FOREIGN KEY (institute_id) REFERENCES institutes (id),
    PRIMARY KEY(institute_id, locale)
);

CREATE TABLE campuses (
    id UUID PRIMARY KEY,
    institute_id VARCHAR(55),
    title VARCHAR(55),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200),
    FOREIGN KEY (institute_id) REFERENCES institutes (id),
    CONSTRAINT campuses_title_constraint UNIQUE (title)
);

CREATE TABLE degree (
    id UUID PRIMARY KEY,
    title VARCHAR(55),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200)
    --CONSTRAINT institutes_title_constraint UNIQUE (title)
);

CREATE TABLE courses (
    id UUID PRIMARY KEY,
    title VARCHAR(55),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200)
    --CONSTRAINT institutes_title_constraint UNIQUE (title)
);

CREATE TABLE boards_grades(
    board_id UUID NOT NULL,
    grade_id UUID NOT NULL,
    PRIMARY KEY(board_id, grade_id),
    FOREIGN KEY (board_id) REFERENCES boards (id),
    FOREIGN KEY (grade_id) REFERENCES grades (id)
);

CREATE TABLE boards_grades_subjects(
    board_id UUID NOT NULL,
    grade_id UUID NOT NULL,
    subject_id UUID NOT NULL,
    PRIMARY KEY(board_id, grade_id, subject_id),
    FOREIGN KEY (board_id) REFERENCES boards (id),
    FOREIGN KEY (grade_id) REFERENCES grades (id),
    FOREIGN KEY (subject_id) REFERENCES subjects (id)
);

CREATE TABLE boards_grades_subjects_books(
    board_id UUID NOT NULL,
    grade_id UUID NOT NULL,
    subject_id UUID NOT NULL,
    book_id UUID NOT NULL,
    PRIMARY KEY(board_id, grade_id, subject_id),
    FOREIGN KEY (board_id) REFERENCES boards (id),
    FOREIGN KEY (grade_id) REFERENCES grades (id),
    FOREIGN KEY (subject_id) REFERENCES subjects (id),
    FOREIGN KEY (book_id) REFERENCES books (id)
);

CREATE TABLE categories (
    id VARCHAR(55) PRIMARY KEY,
    title TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200),
    CONSTRAINT categories_title_constraint UNIQUE (title)
);

CREATE TABLE categories_localized (
    category_id VARCHAR(55),
    locale VARCHAR(8) NOT NULL,
    title TEXT,
    FOREIGN KEY (category_id) REFERENCES categories (id),
    PRIMARY KEY(category_id, locale)
);

CREATE TABLE tags (
    id VARCHAR(55) PRIMARY KEY,
    title TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200),
    CONSTRAINT tags_title_constraint UNIQUE (title)
);

CREATE TABLE tags_localized (
    tag_id VARCHAR(55),
    locale VARCHAR(8) NOT NULL,
    title TEXT,
    FOREIGN KEY (tag_id) REFERENCES tags (id),
    PRIMARY KEY(tag_id, locale)
);


CREATE TABLE events (
    id UUID PRIMARY KEY,
    title VARCHAR(55),
    description TEXT,
    event_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(55) NOT NULL,
    modified_at TIMESTAMP,
    modified_by VARCHAR(200),
    CONSTRAINT event_title_constraint UNIQUE (title)
    --add date field
);


CREATE TABLE events_localized (
    event_id UUID,
    locale VARCHAR(8) NOT NULL,
    title VARCHAR(55),
    description TEXT,
    FOREIGN KEY (event_id) REFERENCES events (id),
    PRIMARY KEY(event_id, locale)
);

CREATE TABLE event_users (
    event_id UUID,
    user_id VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events (id),
    FOREIGN KEY (user_id) REFERENCES learner (user_handle),
    PRIMARY KEY(event_id, user_id)
);