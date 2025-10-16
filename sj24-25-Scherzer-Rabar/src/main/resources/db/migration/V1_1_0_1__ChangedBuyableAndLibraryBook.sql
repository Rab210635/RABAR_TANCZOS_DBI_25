alter table if exists buyable_book add constraint FK_buyable_book_2_publisher foreign key (publisher_id) references publisher;
