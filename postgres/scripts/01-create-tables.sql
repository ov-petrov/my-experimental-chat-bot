-- Скрипт: schema.sql

-- Таблица для хранения системных промптов (создается первой, т.к. на неё ссылается chat)
CREATE TABLE IF NOT EXISTS prompt (
                                      id SERIAL PRIMARY KEY,
                                      type VARCHAR(50) NOT NULL,    -- Например, 'RAG' или 'Expansion'
    name VARCHAR(255) NOT NULL,
    content TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- Таблица для хранения информации о чатах
CREATE TABLE IF NOT EXISTS chat (
                                    id SERIAL PRIMARY KEY,
                                    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- Таблица для хранения записей чата с внешним ключом на таблицу chat.
-- При удалении чата связанные записи будут удаляться (ON DELETE CASCADE).
CREATE TABLE IF NOT EXISTS chat_entry (
                                          id SERIAL PRIMARY KEY,
                                          chat_id INTEGER NOT NULL,
                                          role VARCHAR(50) NOT NULL, -- Например, 'user' или 'assistant'
    content TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat
    FOREIGN KEY(chat_id)
    REFERENCES chat(id)
    ON DELETE CASCADE
    );

-- Таблица загруженных документов
CREATE TABLE IF NOT EXISTS loaded_document (
id SERIAL PRIMARY key,
filename VARCHAR(255) NOT NULL,
content_hash VARCHAR(64) NOT NULL,
document_type VARCHAR(10) NOT NULL,
chunk_count INTEGER,
loaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

CONSTRAINT unique_document UNIQUE(filename, content_hash)
);

-- Индекс для быстрого поиска по имени файла
CREATE INDEX IF NOT EXISTS idx_loaded_documents_filename
ON loaded_document (filename);

-- Векторное расширение для postgres
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Таблица для векторного хранилищ
CREATE TABLE IF NOT EXISTS vector_store (
id VARCHAR(255) PRIMARY KEY,
content TEXT,
metadata JSON,
embedding VECTOR(1024)
);
-- Индекс HNSW для быстрого векторного поиска
CREATE INDEX IF NOT EXISTS vector_store_hnsw_index
ON vector_store USING hnsw (embedding vector_cosine_ops);
