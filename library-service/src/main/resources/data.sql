-- Türler (Genre) tablosuna veri ekleme
INSERT INTO genre (id, created_at, updated_at, name, description) VALUES
                                                                      ('11111111-1111-1111-1111-111111111111', NOW(), NOW(), 'Fiction', 'Fictional stories.'),
                                                                      ('22222222-2222-2222-2222-222222222222', NOW(), NOW(), 'Science Fiction', 'Stories involving science and technology.'),
                                                                      ('33333333-3333-3333-3333-333333333333', NOW(), NOW(), 'History', 'Non-fiction about past events'),
                                                                      ('44444444-4444-4444-4444-444444444444', NOW(), NOW(), 'Mystery', 'Stories involving a puzzling crime.'),
                                                                      ('55555555-5555-5555-5555-555555555555', NOW(), NOW(), 'Fantasy', 'Stories with magical or supernatural elements.'),
                                                                      ('66666666-6666-6666-6666-666666666666', NOW(), NOW(), 'Romance', 'Stories focusing on romantic relationships.'),
                                                                      ('77777777-7777-7777-7777-777777777777', NOW(), NOW(), 'Horror', 'Stories intended to frighten or disturb.'),
                                                                      ('88888888-8888-8888-8888-888888888888', NOW(), NOW(), 'Science', 'Non-fiction about scientific discoveries and principles.'),
                                                                      ('99999999-9999-9999-9999-999999999999', NOW(), NOW(), 'Art', 'Books about visual arts, performing arts, and artists.'),
                                                                      ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW(), 'Children', 'Books written for young readers.');

-- Yazarlar (Author) tablosuna veri ekleme
INSERT INTO author (id, created_at, updated_at, first_name, last_name, "date-of-birth") VALUES
                                                                                            ('a1111111-1111-1111-1111-111111111111', NOW(), NOW(), 'Isaac', 'Asimov', '1920-01-02'),
                                                                                            ('a2222222-2222-2222-2222-222222222222', NOW(), NOW(), 'Agatha', 'Christie', '1890-09-15'),
                                                                                            ('a3333333-3333-3333-3333-333333333333', NOW(), NOW(), 'J.R.R.', 'Tolkien', '1892-01-03'),
                                                                                            ('a4444444-4444-4444-4444-444444444444', NOW(), NOW(), 'Jane', 'Austen', '1775-12-16'),
                                                                                            ('a5555555-5555-5555-5555-555555555555', NOW(), NOW(), 'Stephen', 'King', '1947-09-21'),
                                                                                            ('a6666666-6666-6666-6666-666666666666', NOW(), NOW(), 'Virginia', 'Woolf', '1882-01-25'),
                                                                                            ('a7777777-7777-7777-7777-777777777777', NOW(), NOW(), 'George', 'Orwell', '1903-06-25'),
                                                                                            ('a8888888-8888-8888-8888-888888888888', NOW(), NOW(), 'Haruki', 'Murakami', '1949-01-12'),
                                                                                            ('a9999999-9999-9999-9999-999999999999', NOW(), NOW(), 'J.K.', 'Rowling', '1965-07-31'),
                                                                                            ('aaaaaaaa-1111-2222-3333-444444444444', NOW(), NOW(), 'Orhan', 'Pamuk', '1952-06-07'),
                                                                                            ('bbbbbbbb-1111-2222-3333-444444444444', NOW(), NOW(), 'Elif', 'Şafak', '1971-10-25');

-- Kitaplar (Book) tablosuna veri ekleme
INSERT INTO book (id, created_at, updated_at, title, isbn, status, page, publication_date) VALUES
                                                                                               ('b1111111-1111-1111-1111-111111111111', NOW(), NOW(), 'Foundation', '9780385732957', 'ACTIVE', 256, '1951-06-01'),
                                                                                               ('b2222222-2222-2222-2222-222222222222', NOW(), NOW(), 'Pride and Prejudice', '9780141439518', 'ACTIVE', 279, '1813-01-28'),
                                                                                               ('b3333333-3333-3333-3333-333333333333', NOW(), NOW(), 'The Lord of the Rings', '9780547928227', 'ACTIVE', 1178, '1954-07-29'),
                                                                                               ('b4444444-4444-4444-4444-444444444444', NOW(), NOW(), 'Murder on the Orient Express', '9780007192056', 'ACTIVE', 256, '1934-01-01'),
                                                                                               ('b5555555-5555-5555-5555-555555555555', NOW(), NOW(), 'The Shining', '9780307743657', 'ACTIVE', 447, '1977-01-28'),
                                                                                               ('b6666666-6666-6666-6666-666666666666', NOW(), NOW(), '1984', '9780451524935', 'ACTIVE', 328, '1949-06-08'),
                                                                                               ('b7777777-7777-7777-7777-777777777777', NOW(), NOW(), 'Norwegian Wood', '9780375704024', 'ACTIVE', 296, '1987-09-04'),
                                                                                               ('b8888888-8888-8888-8888-888888888888', NOW(), NOW(), 'Harry Potter and the Philosopher''s Stone', '9780747532743', 'ACTIVE', 223, '1997-06-26'),
                                                                                               ('b9999999-9999-9999-9999-999999999999', NOW(), NOW(), 'My Name is Red', '9780375706851', 'ACTIVE', 417, '1998-11-01'),
                                                                                               ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', NOW(), NOW(), 'The Forty Rules of Love', '9780241972939', 'ACTIVE', 350, '2009-01-01');

-- book_author tablosuna veri ekleme (kitaplar ve yazarlar arasındaki ilişki)
INSERT INTO book_author (book_id, author_id) VALUES
                                                 ((SELECT id FROM book WHERE isbn = '9780385732957'), (SELECT id FROM author WHERE first_name = 'Isaac')),
                                                 ((SELECT id FROM book WHERE isbn = '9780141439518'), (SELECT id FROM author WHERE first_name = 'Jane')),
                                                 ((SELECT id FROM book WHERE isbn = '9780547928227'), (SELECT id FROM author WHERE first_name = 'J.R.R.')),
                                                 ((SELECT id FROM book WHERE isbn = '9780007192056'), (SELECT id FROM author WHERE first_name = 'Agatha')),
                                                 ((SELECT id FROM book WHERE isbn = '9780307743657'), (SELECT id FROM author WHERE first_name = 'Stephen')),
                                                 ((SELECT id FROM book WHERE isbn = '9780451524935'), (SELECT id FROM author WHERE first_name = 'George')),
                                                 ((SELECT id FROM book WHERE isbn = '9780375704024'), (SELECT id FROM author WHERE first_name = 'Haruki')),
                                                 ((SELECT id FROM book WHERE isbn = '9780747532743'), (SELECT id FROM author WHERE first_name = 'J.K.')),
                                                 ((SELECT id FROM book WHERE isbn = '9780375706851'), (SELECT id FROM author WHERE first_name = 'Orhan')),
                                                 ((SELECT id FROM book WHERE isbn = '9780241972939'), (SELECT id FROM author WHERE first_name = 'Elif'));

-- book_genre tablosuna veri ekleme (kitaplar ve türler arasındaki ilişki)
INSERT INTO book_genre (book_id, genre_id) VALUES
                                               ((SELECT id FROM book WHERE isbn = '9780385732957'), (SELECT id FROM genre WHERE name = 'Science Fiction')),
                                               ((SELECT id FROM book WHERE isbn = '9780141439518'), (SELECT id FROM genre WHERE name = 'Fiction')),
                                               ((SELECT id FROM book WHERE isbn = '9780141439518'), (SELECT id FROM genre WHERE name = 'Romance')),
                                               ((SELECT id FROM book WHERE isbn = '9780547928227'), (SELECT id FROM genre WHERE name = 'Fantasy')),
                                               ((SELECT id FROM book WHERE isbn = '9780007192056'), (SELECT id FROM genre WHERE name = 'Mystery')),
                                               ((SELECT id FROM book WHERE isbn = '9780307743657'), (SELECT id FROM genre WHERE name = 'Horror')),
                                               ((SELECT id FROM book WHERE isbn = '9780451524935'), (SELECT id FROM genre WHERE name = 'Science Fiction')),
                                               ((SELECT id FROM book WHERE isbn = '9780375704024'), (SELECT id FROM genre WHERE name = 'Fiction')),
                                               ((SELECT id FROM book WHERE isbn = '9780747532743'), (SELECT id FROM genre WHERE name = 'Fantasy')),
                                               ((SELECT id FROM book WHERE isbn = '9780375706851'), (SELECT id FROM genre WHERE name = 'Fiction'));

-- Kullanıcılar (Users) tablosuna veri ekleme
INSERT INTO users (id, created_at, updated_at, email, password_hash, first_name, last_name, phone_number, address, role, patron_status) VALUES
                                                                                                                                            ('c1111111-1111-1111-1111-111111111111', NOW(), NOW(), 'john.doe@example.com', '$2a$10$x4D/jYaySHO7fDo56RtTTuEGiOQoBsTDOiM1VTrHPvYEEus6Snh8W', 'John', 'Doe', '555-123-4567', '123 Main St', 'PATRON', 'ACTIVE'),
                                                                                                                                            ('c2222222-2222-2222-2222-222222222222', NOW(), NOW(), 'jane.smith@example.com', '$2a$10$x4D/jYaySHO7fDo56RtTTuEGiOQoBsTDOiM1VTrHPvYEEus6Snh8W', 'Jane', 'Smith', '555-987-6543', '456 Oak Ave', 'LIBRARIAN', 'ACTIVE'),
                                                                                                                                            ('c3333333-3333-3333-3333-333333333333', NOW(), NOW(), 'bob.johnson@example.com', '$2a$10$x4D/jYaySHO7fDo56RtTTuEGiOQoBsTDOiM1VTrHPvYEEus6Snh8W', 'Bob', 'Johnson', '555-246-8012', '789 Pine Ln', 'PATRON', 'ACTIVE'),
                                                                                                                                            ('c4444444-4444-4444-4444-444444444444', NOW(), NOW(), 'alice.brown@example.com', '$2a$10$x4D/jYaySHO7fDo56RtTTuEGiOQoBsTDOiM1VTrHPvYEEus6Snh8W', 'Alice', 'Brown', '555-135-7911', '101 Elm St', 'PATRON', 'ACTIVE'),
                                                                                                                                            ('c5555555-5555-5555-5555-555555555555', NOW(), NOW(), 'admin@example.com', '$2a$10$x4D/jYaySHO7fDo56RtTTuEGiOQoBsTDOiM1VTrHPvYEEus6Snh8W', 'Admin', 'User', '555-111-2234', 'Admin Address', 'LIBRARIAN', 'ACTIVE'),
                                                                                                                                            ('c6666666-6666-6666-6666-666666666666', NOW(), NOW(), 'michael.wilson@example.com', '$2a$10$x4D/jYaySHO7fDo56RtTTuEGiOQoBsTDOiM1VTrHPvYEEus6Snh8W', 'Michael', 'Wilson', '555-222-3944', '202 Cedar Dr', 'PATRON', 'ACTIVE'),
                                                                                                                                            ('c7777777-7777-7777-7777-777777777777', NOW(), NOW(), 'emily.clark@example.com', '$2a$10$x4D/jYaySHO7fDo56RtTTuEGiOQoBsTDOiM1VTrHPvYEEus6Snh8W', 'Emily', 'Clark', '555-333-4455', '303 Birch Ave', 'PATRON', 'ACTIVE'),
                                                                                                                                            ('c8888888-8888-8888-8888-888888888888', NOW(), NOW(), 'david.miller@example.com', '$2a$10$x4D/jYaySHO7fDo56RtTTuEGiOQoBsTDOiM1VTrHPvYEEus6Snh8W', 'David', 'Miller', '555-333-5566', '404 Walnut St', 'PATRON', 'ACTIVE'),
                                                                                                                                            ('c9999999-9999-9999-9999-999999999999', NOW(), NOW(), 'sarah.taylor@example.com', '$2a$10$x4D/jYaySHO7fDo56RtTTuEGiOQoBsTDOiM1VTrHPvYEEus6Snh8W', 'Sarah', 'Taylor', '553-555-6677', '505 Maple Rd', 'PATRON', 'ACTIVE'),
                                                                                                                                            ('cccccccc-cccc-cccc-cccc-cccccccccccc', NOW(), NOW(), 'senior.librarian@example.com', '$2a$10$x4D/jYaySHO7fDo56RtTTuEGiOQoBsTDOiM1VTrHPvYEEus6Snh8W', 'Senior', 'Librarian', '551-666-7788', '1616 Library St', 'LIBRARIAN', 'ACTIVE');

-- Kitap Kopyaları (BookCopy) tablosuna veri ekleme
INSERT INTO book_copy (id, created_at, updated_at, barcode, availability_status, book_id) VALUES
                                                                                              ('d1111111-1111-1111-1111-111111111111', NOW(), NOW(), '1234567890123', 'AVAILABLE', (SELECT id FROM book WHERE isbn = '9780385732957')),
                                                                                              ('d2222222-2222-2222-2222-222222222222', NOW(), NOW(), '1234567890124', 'AVAILABLE', (SELECT id FROM book WHERE isbn = '9780385732957')),
                                                                                              ('d3333333-3333-3333-3333-333333333333', NOW(), NOW(), '2345678901234', 'AVAILABLE', (SELECT id FROM book WHERE isbn = '9780141439518')),
                                                                                              ('d4444444-4444-4444-4444-444444444444', NOW(), NOW(), '3456789012345', 'AVAILABLE', (SELECT id FROM book WHERE isbn = '9780547928227')),
                                                                                              ('d5555555-5555-5555-5555-555555555555', NOW(), NOW(), '4567890123456', 'AVAILABLE', (SELECT id FROM book WHERE isbn = '9780007192056')),
                                                                                              ('d6666666-6666-6666-6666-666666666666', NOW(), NOW(), '5678901234567', 'CHECKED_OUT', (SELECT id FROM book WHERE isbn = '9780307743657')),
                                                                                              ('d7777777-7777-7777-7777-777777777777', NOW(), NOW(), '6789012345678', 'AVAILABLE', (SELECT id FROM book WHERE isbn = '9780451524935')),
                                                                                              ('d8888888-8888-8888-8888-888888888888', NOW(), NOW(), '7890123456789', 'CHECKED_OUT', (SELECT id FROM book WHERE isbn = '9780375704024')),
                                                                                              ('d9999999-9999-9999-9999-999999999999', NOW(), NOW(), '8901234567890', 'AVAILABLE', (SELECT id FROM book WHERE isbn = '9780747532743')),
                                                                                              ('dddddddd-dddd-dddd-dddd-dddddddddddd', NOW(), NOW(), '9012345678901', 'AVAILABLE', (SELECT id FROM book WHERE isbn = '9780375706851')),
                                                                                              ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', NOW(), NOW(), '0123456789012', 'CHECKED_OUT', (SELECT id FROM book WHERE isbn = '9780241972939'));

-- WaitList tablosuna veri ekleme
INSERT INTO wait_list (
    id, created_at, updated_at,
    user_id, book_id, start_date, end_date, status
) VALUES
      -- WAITING (end_date NULL)
      ('e1111111-1111-1111-1111-111111111111', NOW(), NOW(),
       (SELECT id FROM users WHERE email = 'john.doe@example.com'),
       (SELECT id FROM book  WHERE isbn  = '9780385732957'),
       NOW() - INTERVAL '2 days',  NULL,                   'WAITING'
      ),
      ('e2222222-2222-2222-2222-222222222222', NOW(), NOW(),
       (SELECT id FROM users WHERE email = 'jane.smith@example.com'),
       (SELECT id FROM book  WHERE isbn  = '9780141439518'),
       NOW() - INTERVAL '4 days',  NULL,                   'WAITING'
      ),
      ('e3333333-3333-3333-3333-333333333333', NOW(), NOW(),
       (SELECT id FROM users WHERE email = 'bob.johnson@example.com'),
       (SELECT id FROM book  WHERE isbn  = '9780547928227'),
       NOW() - INTERVAL '6 days',  NULL,                   'WAITING'
      ),
      ('e4444444-4444-4444-4444-444444444444', NOW(), NOW(),
       (SELECT id FROM users WHERE email = 'alice.brown@example.com'),
       (SELECT id FROM book  WHERE isbn  = '9780007192056'),
       NOW() - INTERVAL '8 days',  NULL,                   'WAITING'
      ),
      -- COMPLETED
      ('e5555555-5555-5555-5555-555555555555', NOW(), NOW(),
       (SELECT id FROM users WHERE email = 'michael.wilson@example.com'),
       (SELECT id FROM book  WHERE isbn  = '9780307743657'),
       NOW() - INTERVAL '22 days', NOW() - INTERVAL '17 days', 'COMPLETED'
      ),
      ('e6666666-6666-6666-6666-666666666666', NOW(), NOW(),
       (SELECT id FROM users WHERE email = 'emily.clark@example.com'),
       (SELECT id FROM book  WHERE isbn  = '9780451524935'),
       NOW() - INTERVAL '24 days', NOW() - INTERVAL '19 days', 'COMPLETED'
      ),
      ('e7777777-7777-7777-7777-777777777777', NOW(), NOW(),
       (SELECT id FROM users WHERE email = 'david.miller@example.com'),
       (SELECT id FROM book  WHERE isbn  = '9780375704024'),
       NOW() - INTERVAL '26 days', NOW() - INTERVAL '21 days', 'COMPLETED'
      ),
      ('e8888888-8888-8888-8888-888888888888', NOW(), NOW(),
       (SELECT id FROM users WHERE email = 'sarah.taylor@example.com'),
       (SELECT id FROM book  WHERE isbn  = '9780747532743'),
       NOW() - INTERVAL '28 days', NOW() - INTERVAL '23 days', 'COMPLETED'
      );

-- Borrowing tablosuna veri ekleme
INSERT INTO borrowing (
    id, created_at, updated_at,
    book_copy_id, user_id, borrowed_by_staff_id,
    borrow_date, due_date, return_date, status
) VALUES
      -- RETURNED
      ('f1111111-1111-1111-1111-111111111111', NOW(), NOW(),
       (SELECT id FROM book_copy WHERE barcode = '1234567890123'),
       (SELECT id FROM users     WHERE email   = 'john.doe@example.com'),
       (SELECT id FROM users     WHERE email   = 'jane.smith@example.com'),
       NOW() - INTERVAL '20 days',
       NOW() - INTERVAL '6 days',
       NOW() - INTERVAL '13 days',
       'RETURNED'
      ),
      ('f2222222-2222-2222-2222-222222222222', NOW(), NOW(),
       (SELECT id FROM book_copy WHERE barcode = '2345678901234'),
       (SELECT id FROM users     WHERE email   = 'bob.johnson@example.com'),
       (SELECT id FROM users     WHERE email   = 'admin@example.com'),
       NOW() - INTERVAL '18 days',
       NOW() - INTERVAL '4 days',
       NOW() - INTERVAL '11 days',
       'RETURNED'
      ),
      ('f3333333-3333-3333-3333-333333333333', NOW(), NOW(),
       (SELECT id FROM book_copy WHERE barcode = '3456789012345'),
       (SELECT id FROM users     WHERE email   = 'alice.brown@example.com'),
       (SELECT id FROM users     WHERE email   = 'senior.librarian@example.com'),
       NOW() - INTERVAL '16 days',
       NOW() - INTERVAL '2 days',
       NOW() - INTERVAL '9 days',
       'RETURNED'
      ),
      ('f4444444-4444-4444-4444-444444444444', NOW(), NOW(),
       (SELECT id FROM book_copy WHERE barcode = '4567890123456'),
       (SELECT id FROM users     WHERE email   = 'michael.wilson@example.com'),
       (SELECT id FROM users     WHERE email   = 'jane.smith@example.com'),
       NOW() - INTERVAL '14 days',
       NOW() /* today */,
       NOW() - INTERVAL '7 days',
       'RETURNED'
      ),
      -- BORROWED (return_date NULL)
      ('f5555555-5555-5555-5555-555555555555', NOW(), NOW(),
       (SELECT id FROM book_copy WHERE barcode = '5678901234567'),
       (SELECT id FROM users     WHERE email   = 'emily.clark@example.com'),
       (SELECT id FROM users     WHERE email   = 'admin@example.com'),
       NOW() - INTERVAL '0 days',
       NOW() + INTERVAL '14 days',
       NULL,
       'BORROWED'
      ),
      ('f6666666-6666-6666-6666-666666666666', NOW(), NOW(),
       (SELECT id FROM book_copy WHERE barcode = '7890123456789'),
       (SELECT id FROM users     WHERE email   = 'david.miller@example.com'),
       (SELECT id FROM users     WHERE email   = 'senior.librarian@example.com'),
       NOW() - INTERVAL '1 days',
       NOW() + INTERVAL '13 days',
       NULL,
       'BORROWED'
      ),
      ('f7777777-7777-7777-7777-777777777777', NOW(), NOW(),
       (SELECT id FROM book_copy WHERE barcode = '0123456789012'),
       (SELECT id FROM users     WHERE email   = 'sarah.taylor@example.com'),
       (SELECT id FROM users     WHERE email   = 'jane.smith@example.com'),
       NOW() - INTERVAL '2 days',
       NOW() + INTERVAL '12 days',
       NULL,
       'BORROWED'
      );


