
CREATE TABLE compiler
(
    id           UUID             NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    language_id  UUID             NOT NULL,
    compiler_id  UUID             NOT NULL,
    cpu_cores    INTEGER          NOT NULL,
    memory_total DOUBLE PRECISION NOT NULL,
    CONSTRAINT pk_compiler PRIMARY KEY (id)
);

CREATE TABLE compiler_heartbeat
(
    id                    UUID             NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE,
    updated_at            TIMESTAMP WITHOUT TIME ZONE,
    compiler_id           UUID             NOT NULL,
    cpu_usage             DOUBLE PRECISION NOT NULL,
    memory_usage          DOUBLE PRECISION NOT NULL,
    total_tasks_processed BIGINT           NOT NULL,
    CONSTRAINT pk_compilerheartbeat PRIMARY KEY (id)
);

CREATE TABLE language
(
    id            UUID         NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    token         VARCHAR(255) NOT NULL,
    language_name VARCHAR(255) NOT NULL,
    description   VARCHAR(255) NOT NULL,
    active        BOOLEAN      NOT NULL,
    CONSTRAINT pk_language PRIMARY KEY (id)
);

CREATE TABLE tag
(
    id         UUID         NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    task_id    UUID         NOT NULL,
    name       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_tag PRIMARY KEY (id)
);

CREATE TABLE task
(
    id              UUID         NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    language_id     UUID         NOT NULL,
    user_created_id UUID,
    user_updated_id UUID,
    name            VARCHAR(255) NOT NULL,
    description     TEXT         NOT NULL,
    difficulty      SMALLINT     NOT NULL,
    template        TEXT         NOT NULL,
    complete        TEXT         NOT NULL,
    active          BOOLEAN      NOT NULL,
    CONSTRAINT pk_task PRIMARY KEY (id)
);

CREATE TABLE task_user_progress
(
    id         UUID     NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    user_id    UUID     NOT NULL,
    task_id    UUID     NOT NULL,
    input      TEXT     NOT NULL,
    state      SMALLINT NOT NULL,
    score      INTEGER  NOT NULL,
    CONSTRAINT pk_task_user_progress PRIMARY KEY (id)
);

CREATE TABLE test
(
    id         UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    task_id    UUID NOT NULL,
    CONSTRAINT pk_test PRIMARY KEY (id)
);

CREATE TABLE test_argument
(
    id                UUID     NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    task_unit_test_id UUID     NOT NULL,
    type              SMALLINT NOT NULL,
    variant           VARCHAR(255),
    value             TEXT     NOT NULL,
    order_index       INTEGER  NOT NULL,
    CONSTRAINT pk_test_argument PRIMARY KEY (id)
);

CREATE TABLE user_settings
(
    user_id        UUID    NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    profile_public BOOLEAN NOT NULL,
    CONSTRAINT pk_user_settings PRIMARY KEY (user_id)
);

CREATE TABLE users
(
    id           UUID                        NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    auth_id      VARCHAR(255)                NOT NULL,
    email        VARCHAR(300)                NOT NULL,
    display_name VARCHAR(255)                NOT NULL,
    role         SMALLINT                    NOT NULL,
    active       BOOLEAN                     NOT NULL,
    last_login   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE task_user_progress
    ADD CONSTRAINT uc_7fe69ee6d550cd3315887c1f9 UNIQUE (user_id, task_id);

ALTER TABLE compiler
    ADD CONSTRAINT uc_compiler_compilerid UNIQUE (compiler_id);

ALTER TABLE language
    ADD CONSTRAINT uc_language_languagename UNIQUE (language_name);

ALTER TABLE language
    ADD CONSTRAINT uc_language_token UNIQUE (token);

ALTER TABLE users
    ADD CONSTRAINT uc_users_authid UNIQUE (auth_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_displayname UNIQUE (display_name);

CREATE INDEX contactEmailContactIdAndName ON task_user_progress (user_id, task_id);

ALTER TABLE compiler_heartbeat
    ADD CONSTRAINT FK_COMPILERHEARTBEAT_ON_COMPILER FOREIGN KEY (compiler_id) REFERENCES compiler (id);

ALTER TABLE compiler
    ADD CONSTRAINT FK_COMPILER_ON_LANGUAGE FOREIGN KEY (language_id) REFERENCES language (id);

ALTER TABLE tag
    ADD CONSTRAINT FK_TAG_ON_TASK FOREIGN KEY (task_id) REFERENCES task (id);

ALTER TABLE task
    ADD CONSTRAINT FK_TASK_ON_LANGUAGE FOREIGN KEY (language_id) REFERENCES language (id);

ALTER TABLE task
    ADD CONSTRAINT FK_TASK_ON_USER_CREATED FOREIGN KEY (user_created_id) REFERENCES users (id);

ALTER TABLE task
    ADD CONSTRAINT FK_TASK_ON_USER_UPDATED FOREIGN KEY (user_updated_id) REFERENCES users (id);

ALTER TABLE task_user_progress
    ADD CONSTRAINT FK_TASK_USER_PROGRESS_ON_TASK FOREIGN KEY (task_id) REFERENCES task (id);

ALTER TABLE task_user_progress
    ADD CONSTRAINT FK_TASK_USER_PROGRESS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE test_argument
    ADD CONSTRAINT FK_TEST_ARGUMENT_ON_TASK_UNIT_TEST FOREIGN KEY (task_unit_test_id) REFERENCES test (id);

ALTER TABLE test
    ADD CONSTRAINT FK_TEST_ON_TASK FOREIGN KEY (task_id) REFERENCES task (id);

ALTER TABLE user_settings
    ADD CONSTRAINT FK_USER_SETTINGS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);