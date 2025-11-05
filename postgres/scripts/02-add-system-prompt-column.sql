-- Скрипт: добавление столбца system_prompt_id в таблицу chat
-- Этот скрипт предназначен для обновления существующей таблицы chat

-- Добавление столбца system_prompt_id в существующую таблицу chat
ALTER TABLE chat 
ADD COLUMN IF NOT EXISTS system_prompt_id INTEGER;

-- Добавление внешнего ключа для system_prompt_id
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'fk_system_prompt'
    ) THEN
        ALTER TABLE chat
        ADD CONSTRAINT fk_system_prompt
        FOREIGN KEY(system_prompt_id)
        REFERENCES prompt(id)
        ON DELETE SET NULL;
    END IF;
END $$;

