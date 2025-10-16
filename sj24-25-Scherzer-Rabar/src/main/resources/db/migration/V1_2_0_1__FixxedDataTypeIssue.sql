alter table book
    ALTER COLUMN release_date SET DATA TYPE date;

alter table borrowing
    alter column from_date set data type date;

alter table libraryorder
    alter column date set data type date;