-- DML-04: Mượn sách có Transaction hoàn chỉnh
BEGIN;
SELECT available_copies FROM books WHERE code = 'B02' FOR UPDATE;

--Mượn sách: giảm available_copies và tạo phiếu mượn.
UPDATE books SET available_copies = available_copies - 1 
WHERE code = 'B02' AND available_copies > 0;

INSERT INTO borrow_tickets (reader_id, book_id, status)
SELECT r.id, b.id, 'BORROWED' FROM readers r, books b 
WHERE r.code = 'R01' AND b.code = 'B02';

SELECT available_copies FROM books WHERE code = 'B02';
COMMIT;

-- Trả sách có Transaction hoàn chỉnh
BEGIN;
SELECT status FROM borrow_tickets WHERE id = 1 FOR UPDATE;

--Trả sách: tăng available_copies và cập nhật phiếu mượn.
UPDATE books SET available_copies = available_copies + 1
WHERE id = (SELECT book_id FROM borrow_tickets WHERE id = 1);

UPDATE borrow_tickets SET status = 'RETURNED', returned_at = CURRENT_TIMESTAMP 
WHERE id = 1 AND status = 'BORROWED';

SELECT * FROM borrow_tickets WHERE id = 1;
COMMIT;
