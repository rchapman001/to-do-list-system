-- Create table for 'list' entity
CREATE TABLE IF NOT EXISTS to_do_list (
    list_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,   -- Auto-incrementing ID
    list_name VARCHAR(255) NOT NULL        -- Column for list name
);
