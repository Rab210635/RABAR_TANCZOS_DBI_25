ALTER TABLE author
    ADD CONSTRAINT unique_penname UNIQUE (penname);

ALTER TABLE author
    ADD CONSTRAINT unique_email_author UNIQUE (email);

ALTER TABLE customer
    ADD CONSTRAINT unique_email_customer UNIQUE (email);