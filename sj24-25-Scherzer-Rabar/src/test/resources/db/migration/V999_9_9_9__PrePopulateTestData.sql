-------------------------------------------------------------------
-- 1. Autoren und zugehörige Adressen (addresses_in_authors)
-------------------------------------------------------------------
-- Autor 1 und Adresse
INSERT INTO author (id, email, penname, first_name, last_name, author_api_key)
VALUES (nextval('author_seq'), 'author1@example.com', 'PenOne', 'Alice', 'Smith', 'api_author_1');
INSERT INTO addresses_in_authors (author_id, zip, city, street_and_number)
VALUES (currval('author_seq'), 1010, 'Vienna', 'Die Coole Straße 105');

-- Autor 2 und Adresse
INSERT INTO author (id, email, penname, first_name, last_name, author_api_key)
VALUES (nextval('author_seq'), 'author2@example.com', 'PenTwo', 'Bob', 'Jones', 'api_author_2');
INSERT INTO addresses_in_authors (author_id, zip, city, street_and_number)
VALUES (currval('author_seq'), 1020, 'Vienna', 'Musterstraße 2');

-- Autor 3 und Adresse
INSERT INTO author (id, email, penname, first_name, last_name, author_api_key)
VALUES (nextval('author_seq'), 'author3@example.com', 'PenThree', 'Carol', 'Taylor', 'api_author_3');
INSERT INTO addresses_in_authors (author_id, zip, city, street_and_number)
VALUES (currval('author_seq'), 1030, 'Graz', 'Hauptstraße 3');

-- Autor 4 und Adresse
INSERT INTO author (id, email, penname, first_name, last_name, author_api_key)
VALUES (nextval('author_seq'), 'author4@example.com', 'PenFour', 'David', 'Brown', 'api_author_4');
INSERT INTO addresses_in_authors (author_id, zip, city, street_and_number)
VALUES (currval('author_seq'), 1040, 'Linz', 'Nebenstraße 4');

-- Autor 5 und Adresse
INSERT INTO author (id, email, penname, first_name, last_name, author_api_key)
VALUES (nextval('author_seq'), 'author5@example.com', 'PenFive', 'Eve', 'Davis', 'api_author_5');
INSERT INTO addresses_in_authors (author_id, zip, city, street_and_number)
VALUES (currval('author_seq'), 1050, 'Salzburg', 'Ringstraße 5');

-------------------------------------------------------------------
-- 2. Kunden und zugehörige Adressen (addresses_in_customers)
-------------------------------------------------------------------
-- Kunde 1 und Adresse
INSERT INTO customer (id, email, first_name, last_name, customer_api_key)
VALUES (nextval('customer_seq'), 'customer1@example.com', 'John', 'Doe', 'api_customer_1');
INSERT INTO addresses_in_customers (customer_id, zip, city, street_and_number)
VALUES (currval('customer_seq'), 2010, 'Vienna', 'Favoritenstraße 1');

-- Kunde 2 und Adresse
INSERT INTO customer (id, email, first_name, last_name, customer_api_key)
VALUES (nextval('customer_seq'), 'customer2@example.com', 'Jane', 'Roe', 'api_customer_2');
INSERT INTO addresses_in_customers (customer_id, zip, city, street_and_number)
VALUES (currval('customer_seq'), 2020, 'Graz', 'Lendgasse 2');

-- Kunde 3 und Adresse
INSERT INTO customer (id, email, first_name, last_name, customer_api_key)
VALUES (nextval('customer_seq'), 'customer3@example.com', 'Jim', 'Beam', 'api_customer_3');
INSERT INTO addresses_in_customers (customer_id, zip, city, street_and_number)
VALUES (currval('customer_seq'), 2030, 'Linz', 'Donaugasse 3');

-- Kunde 4 und Adresse
INSERT INTO customer (id, email, first_name, last_name, customer_api_key)
VALUES (nextval('customer_seq'), 'customer4@example.com', 'Jill', 'Stark', 'api_customer_4');
INSERT INTO addresses_in_customers (customer_id, zip, city, street_and_number)
VALUES (currval('customer_seq'), 2040, 'Salzburg', 'Mozartweg 4');

-- Kunde 5 und Adresse
INSERT INTO customer (id, email, first_name, last_name, customer_api_key)
VALUES (nextval('customer_seq'), 'customer5@example.com', 'Jack', 'Black', 'api_customer_5');
INSERT INTO addresses_in_customers (customer_id, zip, city, street_and_number)
VALUES (currval('customer_seq'), 2050, 'Innsbruck', 'Tiroler Straße 5');

-------------------------------------------------------------------
-- 3. Publisher
-------------------------------------------------------------------
INSERT INTO publisher (id, zip, city, name, street_and_number, publisher_api_key)
VALUES (nextval('publisher_seq'), 10001, 'New York', 'Publisher One', '123 Publisher St', 'api_publisher_1');
INSERT INTO publisher (id, zip, city, name, street_and_number, publisher_api_key)
VALUES (nextval('publisher_seq'), 20002, 'Los Angeles', 'Publisher Two', '456 Media Rd', 'api_publisher_2');
INSERT INTO publisher (id, zip, city, name, street_and_number, publisher_api_key)
VALUES (nextval('publisher_seq'), 30003, 'Chicago', 'Publisher Three', '789 Print Ave', 'api_publisher_3');
INSERT INTO publisher (id, zip, city, name, street_and_number, publisher_api_key)
VALUES (nextval('publisher_seq'), 40004, 'Houston', 'Publisher Four', '101 Publish Blvd', 'api_publisher_4');
INSERT INTO publisher (id, zip, city, name, street_and_number, publisher_api_key)
VALUES (nextval('publisher_seq'), 50005, 'Phoenix', 'Publisher Five', '202 Book Ln', 'api_publisher_5');

-------------------------------------------------------------------
-- 4. Libraries
-------------------------------------------------------------------
INSERT INTO library (id, zip, city, name, street_and_number, library_api_key)
VALUES (nextval('library_seq'), 11111, 'Boston', 'Central Library', '1 Library Ave', 'api_library_1');
INSERT INTO library (id, zip, city, name, street_and_number, library_api_key)
VALUES (nextval('library_seq'), 22222, 'Seattle', 'North Library', '2 Library Ave', 'api_library_2');
INSERT INTO library (id, zip, city, name, street_and_number, library_api_key)
VALUES (nextval('library_seq'), 33333, 'Denver', 'East Library', '3 Library Ave', 'api_library_3');
INSERT INTO library (id, zip, city, name, street_and_number, library_api_key)
VALUES (nextval('library_seq'), 44444, 'Miami', 'South Library', '4 Library Ave', 'api_library_4');
INSERT INTO library (id, zip, city, name, street_and_number, library_api_key)
VALUES (nextval('library_seq'), 55555, 'Atlanta', 'West Library', '5 Library Ave', 'api_library_5');

-------------------------------------------------------------------
-- 5. Books
-------------------------------------------------------------------
INSERT INTO book (id, name, word_count, description, release_date, available_online, book_api_key)
VALUES (nextval('book_seq'), 'Book One', 150, 'Description for Book One', '2020-01-01', TRUE, 'api_book_1');
INSERT INTO book (id, name, word_count, description, release_date, available_online, book_api_key)
VALUES (nextval('book_seq'), 'Book Two', 200, 'Description for Book Two', '2020-02-01', FALSE, 'api_book_2');
INSERT INTO book (id, name, word_count, description, release_date, available_online, book_api_key)
VALUES (nextval('book_seq'), 'Book Three', 250, 'Description for Book Three', '2020-03-01', TRUE, 'api_book_3');
INSERT INTO book (id, name, word_count, description, release_date, available_online, book_api_key)
VALUES (nextval('book_seq'), 'Book Four', 300, 'Description for Book Four', '2020-04-01', FALSE, 'api_book_4');
INSERT INTO book (id, name, word_count, description, release_date, available_online, book_api_key)
VALUES (nextval('book_seq'), 'Book Five', 350, 'Description for Book Five', '2020-05-01', TRUE, 'api_book_5');

-------------------------------------------------------------------
-- 6. Authors of Book (Verknüpfung von Autor und Book)
-------------------------------------------------------------------
-- Hier wird angenommen, dass für jeden Autor auch das jeweils korrespondierende Buch existiert.
INSERT INTO authors_of_book (author_id, book_id) VALUES (1, 1);
INSERT INTO authors_of_book (author_id, book_id) VALUES (51, 51);
INSERT INTO authors_of_book (author_id, book_id) VALUES (101, 101);
INSERT INTO authors_of_book (author_id, book_id) VALUES (151, 151);
INSERT INTO authors_of_book (author_id, book_id) VALUES (201, 201);

-------------------------------------------------------------------
-- 7. Book Types
-------------------------------------------------------------------
INSERT INTO book_types (book_id, book_types) VALUES (1, 'H');
INSERT INTO book_types (book_id, book_types) VALUES (51, 'E');
INSERT INTO book_types (book_id, book_types) VALUES (101, 'E');
INSERT INTO book_types (book_id, book_types) VALUES (151, 'H');
INSERT INTO book_types (book_id, book_types) VALUES (201, 'E');

-------------------------------------------------------------------
-- 8. Genres of Book
-------------------------------------------------------------------
INSERT INTO genres_of_book (book_id, genre_code) VALUES (1, 'TH');
INSERT INTO genres_of_book (book_id, genre_code) VALUES (51, 'TH');
INSERT INTO genres_of_book (book_id, genre_code) VALUES (101, 'CR');
INSERT INTO genres_of_book (book_id, genre_code) VALUES (151, 'RO');
INSERT INTO genres_of_book (book_id, genre_code) VALUES (201, 'FA');

-------------------------------------------------------------------
-- 9. Library Orders
-------------------------------------------------------------------
INSERT INTO libraryorder (id, customer_id, date, libraryOrder_api_key)
VALUES (nextval('libraryorder_seq'), 1, '2020-06-01', 'api_order_1');
INSERT INTO libraryorder (id, customer_id, date, libraryOrder_api_key)
VALUES (nextval('libraryorder_seq'), 51, '2020-06-15', 'api_order_2');
INSERT INTO libraryorder (id, customer_id, date, libraryOrder_api_key)
VALUES (nextval('libraryorder_seq'), 101, '2020-07-01', 'api_order_3');
INSERT INTO libraryorder (id, customer_id, date, libraryOrder_api_key)
VALUES (nextval('libraryorder_seq'), 151, '2020-07-15', 'api_order_4');
INSERT INTO libraryorder (id, customer_id, date, libraryOrder_api_key)
VALUES (nextval('libraryorder_seq'), 201, '2020-08-01', 'api_order_5');

-------------------------------------------------------------------
-- 10. Branches
-------------------------------------------------------------------
-- Hier wird vorausgesetzt, dass der zugehörige Library-Datensatz existiert.
INSERT INTO branch (id, library_id, zip, city, street_and_number, branch_api_key)
VALUES (nextval('branch_seq'), 1, 11112, 'Boston', '11 Branch Rd', 'api_branch_1');
INSERT INTO branch (id, library_id, zip, city, street_and_number, branch_api_key)
VALUES (nextval('branch_seq'), 51, 22223, 'Seattle', '22 Branch Rd', 'api_branch_2');
INSERT INTO branch (id, library_id, zip, city, street_and_number, branch_api_key)
VALUES (nextval('branch_seq'), 101, 33334, 'Denver', '33 Branch Rd', 'api_branch_3');
INSERT INTO branch (id, library_id, zip, city, street_and_number, branch_api_key)
VALUES (nextval('branch_seq'), 151, 44445, 'Miami', '44 Branch Rd', 'api_branch_4');
INSERT INTO branch (id, library_id, zip, city, street_and_number, branch_api_key)
VALUES (nextval('branch_seq'), 201, 55556, 'Atlanta', '55 Branch Rd', 'api_branch_5');

-------------------------------------------------------------------
-- 11. Borrowings
-------------------------------------------------------------------
-- customer_borrowing verweist auf einen bestehenden Kunden.
INSERT INTO borrowing (id, from_date, extended_by_days, customer_borrowing, borrowing_api_key)
VALUES (nextval('borrowing_seq'), '2020-09-01', 5, 1, 'api_borrowing_1');
INSERT INTO borrowing (id, from_date, extended_by_days, customer_borrowing, borrowing_api_key)
VALUES (nextval('borrowing_seq'), '2020-09-15', 7, 51, 'api_borrowing_2');
INSERT INTO borrowing (id, from_date, extended_by_days, customer_borrowing, borrowing_api_key)
VALUES (nextval('borrowing_seq'), '2020-10-01', 3, 101, 'api_borrowing_3');
INSERT INTO borrowing (id, from_date, extended_by_days, customer_borrowing, borrowing_api_key)
VALUES (nextval('borrowing_seq'), '2020-10-15', 10, 151, 'api_borrowing_4');
INSERT INTO borrowing (id, from_date, extended_by_days, customer_borrowing, borrowing_api_key)
VALUES (nextval('borrowing_seq'), '2020-11-01', 2, 201, 'api_borrowing_5');

-------------------------------------------------------------------
-- 12. Books in Library
-------------------------------------------------------------------
-- Verknüpft Book und Library
INSERT INTO books_in_library (book_id, library_id, borrow_length_days)
VALUES (1, 1, 14);
INSERT INTO books_in_library (book_id, library_id, borrow_length_days)
VALUES (51, 51, 21);
INSERT INTO books_in_library (book_id, library_id, borrow_length_days)
VALUES (101, 101, 7);
INSERT INTO books_in_library (book_id, library_id, borrow_length_days)
VALUES (151, 151, 30);
INSERT INTO books_in_library (book_id, library_id, borrow_length_days)
VALUES (201, 201, 10);

-------------------------------------------------------------------
-- 13. Buyable Books
-------------------------------------------------------------------
-- Felder: book_id, buyable_book_id, price, book_type, page_count, publisher_id, buyableBook_api_key
INSERT INTO buyable_book (book_id, buyable_book_id, price, book_type, page_count, publisher_id, buyableBook_api_key)
VALUES (1, nextval('buyable_book_seq'), 10.99, 'H', 200, 1, 'api_buyable_1');
INSERT INTO buyable_book (book_id, buyable_book_id, price, book_type, page_count, publisher_id, buyableBook_api_key)
VALUES (51, nextval('buyable_book_seq'), 12.99, 'E', 250, 51, 'api_buyable_2');
INSERT INTO buyable_book (book_id, buyable_book_id, price, book_type, page_count, publisher_id, buyableBook_api_key)
VALUES (101, nextval('buyable_book_seq'), 15.99, 'E', 300, 101, 'api_buyable_3');
INSERT INTO buyable_book (book_id, buyable_book_id, price, book_type, page_count, publisher_id, buyableBook_api_key)
VALUES (151, nextval('buyable_book_seq'), 9.99, 'H', 220, 151, 'api_buyable_4');
INSERT INTO buyable_book (book_id, buyable_book_id, price, book_type, page_count, publisher_id, buyableBook_api_key)
VALUES (201, nextval('buyable_book_seq'), 14.99, 'E', 280, 201, 'api_buyable_5');

-------------------------------------------------------------------
-- 14. Copies
-------------------------------------------------------------------
-- Felder: id, book_id, publisher_id, book_type, page_count, copies_borrowed, copy_api_key, branch_id
-- Hier wird als copies_borrowed ein existierender Borrowing-Wert genutzt.
INSERT INTO copy (id, book_id, publisher_id, book_type, page_count, copies_borrowed, copy_api_key, branch_id)
VALUES (nextval('copy_seq'), 1, 1, 'H', 200, 1, 'api_copy_1', 1);
INSERT INTO copy (id, book_id, publisher_id, book_type, page_count, copies_borrowed, copy_api_key, branch_id)
VALUES (nextval('copy_seq'), 51, 51, 'E', 250, 51, 'api_copy_2', 51);
INSERT INTO copy (id, book_id, publisher_id, book_type, page_count, copies_borrowed, copy_api_key, branch_id)
VALUES (nextval('copy_seq'), 101, 101, 'E', 300, 101, 'api_copy_3', 101);
INSERT INTO copy (id, book_id, publisher_id, book_type, page_count, copies_borrowed, copy_api_key, branch_id)
VALUES (nextval('copy_seq'), 151, 151, 'H', 220, 151, 'api_copy_4', 151);
INSERT INTO copy (id, book_id, publisher_id, book_type, page_count, copies_borrowed, copy_api_key, branch_id)
VALUES (nextval('copy_seq'), 201, 201, 'E', 280, 201, 'api_copy_5', 201);

-------------------------------------------------------------------
-- 15. Library Subscriptions
-------------------------------------------------------------------
INSERT INTO librarysubscription (id, library_id, name, description, monthly_cost, librarySubscription_api_key)
VALUES (nextval('librarysubscription_seq'), 1, 'Sub One', 'Basic subscription', 9.99, 'api_subscription_1');
INSERT INTO librarysubscription (id, library_id, name, description, monthly_cost, librarySubscription_api_key)
VALUES (nextval('librarysubscription_seq'), 51, 'Sub Two', 'Standard subscription', 14.99, 'api_subscription_2');
INSERT INTO librarysubscription (id, library_id, name, description, monthly_cost, librarySubscription_api_key)
VALUES (nextval('librarysubscription_seq'), 101, 'Sub Three', 'Premium subscription', 19.99, 'api_subscription_3');
INSERT INTO librarysubscription (id, library_id, name, description, monthly_cost, librarySubscription_api_key)
VALUES (nextval('librarysubscription_seq'), 151, 'Sub Four', 'Family subscription', 24.99, 'api_subscription_4');
INSERT INTO librarysubscription (id, library_id, name, description, monthly_cost, librarySubscription_api_key)
VALUES (nextval('librarysubscription_seq'), 201, 'Sub Five', 'Student subscription', 7.99, 'api_subscription_5');

-------------------------------------------------------------------
-- 16. Subscriptions in Order
-------------------------------------------------------------------
-- Verknüpft Libraryorder und Librarysubscription
INSERT INTO subscriptions_in_order (order_id, subscription_id) VALUES (1, 1);
INSERT INTO subscriptions_in_order (order_id, subscription_id) VALUES (51, 51);
INSERT INTO subscriptions_in_order (order_id, subscription_id) VALUES (101, 101);
INSERT INTO subscriptions_in_order (order_id, subscription_id) VALUES (151, 151);
INSERT INTO subscriptions_in_order (order_id, subscription_id) VALUES (201, 201);

-------------------------------------------------------------------
-- 17. Reviews
-------------------------------------------------------------------
-- Felder: book_id, branch_id, customer_id, publisher_id, review_id, title, rating, description, review_api_key
INSERT INTO review (book_id, branch_id, customer_id, publisher_id, review_id, title, rating, description, review_api_key)
VALUES (1, 1, 1, 1, nextval('review_seq'), 'Great Book One', 5, 'An excellent read for beginners.', 'api_review_1');
INSERT INTO review (book_id, branch_id, customer_id, publisher_id, review_id, title, rating, description, review_api_key)
VALUES (51, 51, 51, 51, nextval('review_seq'), 'Great Book Two', 4, 'Very enjoyable and insightful.', 'api_review_2');
INSERT INTO review (book_id, branch_id, customer_id, publisher_id, review_id, title, rating, description, review_api_key)
VALUES (101, 101, 101, 101, nextval('review_seq'), 'Great Book Three', 3, 'Good, but could be improved.', 'api_review_3');
INSERT INTO review (book_id, branch_id, customer_id, publisher_id, review_id, title, rating, description, review_api_key)
VALUES (151, 151, 151, 151, nextval('review_seq'), 'Great Book Four', 5, 'Loved every chapter.', 'api_review_4');
INSERT INTO review (book_id, branch_id, customer_id, publisher_id, review_id, title, rating, description, review_api_key)
VALUES (201, 201, 201, 201, nextval('review_seq'), 'Great Book Five', 4, 'A solid work of literature.', 'api_review_5');

-------------------------------------------------------------------
-- 18. Books in Order
-------------------------------------------------------------------
-- Verknüpft Buyable Book und Libraryorder
INSERT INTO book_in_order (book_id, order_id) VALUES (1, 1);
INSERT INTO book_in_order (book_id, order_id) VALUES (51, 51);
INSERT INTO book_in_order (book_id, order_id) VALUES (101, 101);
INSERT INTO book_in_order (book_id, order_id) VALUES (151, 151);
INSERT INTO book_in_order (book_id, order_id) VALUES (201, 201);
