drop table if exists hits;
create TABLE  IF NOT EXISTS hits
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app       VARCHAR(32),
    uri       VARCHAR(128),
    ip        VARCHAR(16),
    timestamp timestamp WITHOUT TIME ZONE
);
