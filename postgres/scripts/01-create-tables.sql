-- Скрипт: schema.sql

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

-- Таблица для хранения системных промптов
CREATE TABLE IF NOT EXISTS prompt (
                                      id SERIAL PRIMARY KEY,
                                      type VARCHAR(50) NOT NULL,    -- Например, 'RAG' или 'Expansion'
    name VARCHAR(255) NOT NULL,
    content TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );
