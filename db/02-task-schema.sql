-- Create table for 'task' entity
CREATE TABLE IF NOT EXISTS task (
    task_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,        -- Auto-incrementing ID
    list_id BIGINT NOT NULL,              -- Foreign key for list ID
    task_name VARCHAR(255) NOT NULL,     -- Column for task name
    task_date DATE,                      -- Column for task date
    task_status VARCHAR(20) NOT NULL,    -- Column for task status (stored as STRING)
    CONSTRAINT fk_list FOREIGN KEY (list_id) REFERENCES to_do_list(list_id) -- Foreign key constraint
);
