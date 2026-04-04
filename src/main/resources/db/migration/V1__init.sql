-- init.sql

CREATE TABLE IF NOT EXISTS users (
                                     id       BIGSERIAL PRIMARY KEY,
                                     email    VARCHAR(255) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     role     VARCHAR(50)  NOT NULL
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              id         BIGSERIAL PRIMARY KEY,
                                              token      VARCHAR(255) NOT NULL UNIQUE,
                                              user_id    BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                                              expires_at TIMESTAMP    NOT NULL,
                                              revoked    BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS persons (
                                       id                   BIGSERIAL PRIMARY KEY,
                                       birth_year           INTEGER,
                                       death_year           INTEGER,
                                       birth_date           DATE,
                                       death_date           DATE,
                                       image_name           VARCHAR(255),
                                       arrest_date          DATE,
                                       sentence_date        DATE,
                                       rehabilitation_date  DATE,
                                       status               SMALLINT  NOT NULL DEFAULT 0,
                                       normalized_name      VARCHAR(255) UNIQUE,
                                       created_at           TIMESTAMP,
                                       updated_at           TIMESTAMP
);

CREATE TABLE IF NOT EXISTS person_translations (
                                                   id          BIGSERIAL PRIMARY KEY,
                                                   person_id   BIGINT       NOT NULL REFERENCES persons (id) ON DELETE CASCADE,
                                                   language    SMALLINT     NOT NULL,
                                                   full_name   VARCHAR(255),
                                                   birth_place VARCHAR(255),
                                                   death_place VARCHAR(255),
                                                   region      VARCHAR(255),
                                                   district    VARCHAR(255),
                                                   occupation  VARCHAR(255),
                                                   charge      TEXT,
                                                   sentence    TEXT,
                                                   biography   TEXT,
                                                   UNIQUE (person_id, language)
);

CREATE TABLE IF NOT EXISTS documents (
                                         id             BIGSERIAL PRIMARY KEY,
                                         original_name  VARCHAR(255),
                                         file_name      VARCHAR(255),
                                         extracted_text TEXT,
                                         uploaded_at    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS person_documents (
                                                person_id   BIGINT NOT NULL REFERENCES persons (id) ON DELETE CASCADE,
                                                document_id BIGINT NOT NULL REFERENCES documents (id) ON DELETE CASCADE,
                                                PRIMARY KEY (person_id, document_id)
);

CREATE TABLE IF NOT EXISTS chats (
                                     id         BIGSERIAL PRIMARY KEY,
                                     title      VARCHAR(255),
                                     user_id    BIGINT    NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                                     created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS messages (
                                        id           BIGSERIAL PRIMARY KEY,
                                        chat_id      BIGINT    NOT NULL REFERENCES chats (id) ON DELETE CASCADE,
                                        question     TEXT      NOT NULL,
                                        answer       TEXT      NOT NULL,
                                        sources_json TEXT,
                                        created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- индексы
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id     ON refresh_tokens (user_id);
CREATE INDEX IF NOT EXISTS idx_person_translations_person  ON person_translations (person_id);
CREATE INDEX IF NOT EXISTS idx_person_documents_person     ON person_documents (person_id);
CREATE INDEX IF NOT EXISTS idx_person_documents_document   ON person_documents (document_id);
CREATE INDEX IF NOT EXISTS idx_chats_user_id               ON chats (user_id);
CREATE INDEX IF NOT EXISTS idx_messages_chat_id            ON messages (chat_id);
CREATE INDEX IF NOT EXISTS idx_persons_normalized_name     ON persons (normalized_name);
CREATE INDEX IF NOT EXISTS idx_persons_status              ON persons (status);