-- DML-04: Mượn sách có Transaction hoàn chỉnh
BEGIN;
-- Khóa dòng dữ liệu chống tranh chấp
SELECT available_copies FROM books WHERE code = 'B02' FOR UPDATE;

-- Gom UPDATE và INSERT vào cùng một statement
WITH updated_book AS (
    UPDATE books 
    SET available_copies = available_copies - 1 
    WHERE code = 'B02' AND available_copies > 0
    RETURNING id
)
INSERT INTO borrow_tickets (reader_id, book_id, status)
SELECT 
    (SELECT id FROM readers WHERE code = 'R01'), 
    u.id, 
    'BORROWED'
FROM updated_book u;

COMMIT;

BEGIN;
-- Khóa vé mượn
SELECT status FROM borrow_tickets WHERE id = 1 FOR UPDATE;

-- Gom UPDATE vé và UPDATE tồn kho vào cùng một statement, sau đấy mới cập nhật tồn kho
WITH updated_ticket AS (
    UPDATE borrow_tickets 
    SET status = 'RETURNED', returned_at = CURRENT_TIMESTAMP 
    WHERE id = 1 AND status = 'BORROWED'
    RETURNING book_id
)
UPDATE books 
SET available_copies = available_copies + 1
FROM updated_ticket
WHERE books.id = updated_ticket.book_id 
  AND books.available_copies < books.total_copies;

COMMIT;
