ALTER TABLE buyable_book
    ADD COLUMN publisher_id BIGINT NOT NULL;



ALTER TABLE buyable_book
    ADD COLUMN page_count INTEGER NOT NULL CHECK (page_count BETWEEN 3 AND 2147483647);

ALTER TABLE buyable_book
    RENAME COLUMN version TO book_type;

ALTER TABLE buyable_book
    ALTER COLUMN book_type SET DATA TYPE CHAR(1);

alter table buyable_book
add CONSTRAINT book_type_check CHECK (book_type IN ('H', 'P', 'E'));

