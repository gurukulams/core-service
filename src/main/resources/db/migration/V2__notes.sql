CREATE TABLE annotation (
   id UUID PRIMARY KEY,
   on_type VARCHAR NOT NULL,
   on_instance VARCHAR NOT NULL,
   locale VARCHAR(8),
   note JSON NOT NULL,
   created_by VARCHAR(55) NOT NULL
);
