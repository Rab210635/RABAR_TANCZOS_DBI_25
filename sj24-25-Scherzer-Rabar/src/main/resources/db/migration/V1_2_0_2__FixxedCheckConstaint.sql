ALTER TABLE genres_of_book DROP CONSTRAINT genres_of_book_genre_code_check;
ALTER TABLE genres_of_book
    ADD CONSTRAINT genre_code_check
        CHECK (genre_code IN ('MY','TH','CR','RO','FA','SF','HF','CF','YA','BI','AU','ME','SH','TC','HI','SC','TE','PH','RE','SP','GN','CO','PO','HO'));
