CREATE TABLE IF NOT EXISTS asset (
    id VARCHAR(255) PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    url VARCHAR(512),
    size BIGINT NOT NULL,
    upload_date TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL
);