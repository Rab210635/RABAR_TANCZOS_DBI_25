create sequence author_seq start with 1 increment by 50;
create sequence book_seq start with 1 increment by 50;
create sequence borrowing_seq start with 1 increment by 50;
create sequence branch_seq start with 1 increment by 50;
create sequence buyable_book_seq start with 1 increment by 50;
create sequence copy_seq start with 1 increment by 50;
create sequence customer_seq start with 1 increment by 50;
create sequence library_seq start with 1 increment by 50;
create sequence libraryorder_seq start with 1 increment by 50;
create sequence librarysubscription_seq start with 1 increment by 50;
create sequence publisher_seq start with 1 increment by 50;
create sequence review_seq start with 1 increment by 50;
create table addresses_in_authors
(
    author_id         bigint not null,
    zip               integer,
    city              varchar(255),
    street_and_number varchar(255)
);
create table addresses_in_customers
(
    customer_id       bigint not null,
    zip               integer,
    city              varchar(255),
    street_and_number varchar(255)
);
create table author
(
    id         bigint not null,
    email      varchar(255),
    penname    varchar(255),
    first_name varchar(255),
    last_name  varchar(255),
    primary key (id)
);
create table authors_of_book
(
    author_id bigint not null,
    book_id   bigint not null
);
create table book
(
    id               bigint       not null,
    name             varchar(255) not null,
    word_count       integer      not null check ((word_count >= 100) and (word_count <= 2147483647)),
    description      varchar(255),
    release_date     timestamp(6),
    available_online boolean      not null,
    primary key (id)
);
create table book_in_order
(
    book_id  bigint not null,
    order_id bigint not null
);
create table books_in_library
(
    book_id            bigint,
    library_id         bigint not null
    borrow_length_days integer,
);
create table book_types
(
    book_id    bigint not null
    book_types char(1) check(type in ( 'H','P','E')),
);
create table borrowing
(
    id                 bigint  not null,
    from_date          timestamp(6),
    extended_by_days   integer not null check ((extended_by_days >= 1) and (extended_by_days <= 30)),
    customer_borrowing bigint,
    primary key (id)
);
create table branch
(
    id                bigint not null,
    library_id        bigint,
    zip               integer,
    city              varchar(255),
    street_and_number varchar(255),
    primary key (id)
);
create table buyable_book
(
    book_id         bigint,
    buyable_book_id bigint not null,
    price           float(24) check ((price >= 1) and (price <= 2147483647)),
    version         char(1),
    primary key (buyable_book_id)
);
create table copy
(
    id              bigint  not null,
    book_id         bigint  not null,
    publisher_id    bigint  not null,
    book_type       char(1) check(book_type in ( 'H','P','E')) not null,
    page_count      integer not null check ((page_count >= 3) and (page_count <= 2147483647)),
    copies_borrowed bigint,
    primary key (id)
);
create table customer
(
    id         bigint not null,
    email      varchar(255),
    first_name varchar(255),
    last_name  varchar(255),
    primary key (id)
);
create table genres_of_book
(
    book_id    bigint not null,
    genre_code char(2) check ( genre_code in ('MI','TH','CR','RO','FA','SF','HF','CF','YA','BI','AU','ME','SH','TC','HI','SC','TE','PH','RE','SP','GN','CO','PO','HO'))
);
create table library
(
    id                bigint       not null,
    zip               integer,
    city              varchar(255),
    name              varchar(255) not null,
    street_and_number varchar(255),
    primary key (id)
);
create table libraryorder
(
    id          bigint       not null,
    customer_id bigint       not null,
    date        timestamp(6) not null,
    primary key (id)
);
create table librarysubscription
(
    id           bigint       not null,
    library_id   bigint       not null,
    name         varchar(255) not null,
    description  varchar(255),
    monthly_cost float(53)    not null check ((monthly_cost >= 0) and (monthly_cost <= 10000)),
    primary key (id)
);
create table publisher
(
    id                bigint       not null,
    zip               integer,
    city              varchar(255),
    name              varchar(255) not null,
    street_and_number varchar(255),
    primary key (id)
);
create table review
(
    book_id      bigint,
    branch_id    bigint,
    customer_id  bigint  not null,
    publisher_id bigint,
    review_id    bigint  not null,
    title        varchar(255),
    rating       integer not null,
    description  varchar(255),
    primary key (review_id)
);
create table subscriptions_in_order
(
    order_id        bigint not null,
    subscription_id bigint not null
);
alter table if exists addresses_in_authors add constraint FK_adresses_2_author foreign key (author_id) references author;
alter table if exists addresses_in_customers add constraint FK_adresses_2_customer foreign key (customer_id) references customer;
alter table if exists authors_of_book add constraint FK_authors_2_books foreign key (author_id) references author;
alter table if exists authors_of_book add constraint FK_books_2_authors foreign key (book_id) references book;
alter table if exists book_in_order add constraint FK_orderbook foreign key (book_id) references buyable_book;
alter table if exists book_in_order add constraint FK_book foreign key (order_id) references libraryorder;
alter table if exists books_in_library add constraint books_in_libraries_2_book foreign key (book_id) references book;
alter table if exists books_in_library add constraint FK_books_in_libraries_2_library foreign key (library_id) references library;
alter table if exists book_types add constraint FK_book_types_2_book foreign key (book_id) references book;
alter table if exists borrowing add constraint FK_borrowings_2_customer foreign key (customer_borrowing) references customer;
alter table if exists branch add constraint FK_branches_2_library foreign key (library_id) references library;
alter table if exists buyable_book add constraint FK_buyable_books_2_book foreign key (book_id) references book;
alter table if exists copy add constraint FK_copies_2_book foreign key (book_id) references book;
alter table if exists copy add constraint FK_copies_2_publisher foreign key (publisher_id) references publisher;
alter table if exists copy add constraint FK_borrowing_2_copies foreign key (copies_borrowed) references borrowing;
alter table if exists genres_of_book add constraint FK_genres_2_book foreign key (book_id) references book;
alter table if exists libraryorder add constraint FK_customer_order foreign key (customer_id) references customer;
alter table if exists librarysubscription add constraint library_subscriptions_2_library foreign key (library_id) references library;
alter table if exists review add constraint FK_reviews_2_book foreign key (book_id) references book;
alter table if exists review add constraint FK_reviews_2_branch foreign key (branch_id) references branch;
alter table if exists review add constraint FK_reviews_2_customer foreign key (customer_id) references customer;
alter table if exists review add constraint FK_reviews_2_publisher foreign key (publisher_id) references publisher;
alter table if exists subscriptions_in_order add constraint FK_subscription foreign key (subscription_id) references librarysubscription;
alter table if exists subscriptions_in_order add constraint FK_ordersubscription foreign key (order_id) references libraryorder;