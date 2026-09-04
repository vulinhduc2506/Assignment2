-- DML-04: Mượn sách có Transaction hoàn chỉnh
BEGIN;
SELECT available_copies FROM books WHERE code = 'B02' FOR UPDATE;

--Mượn sách: giảm available_copies và tạo phiếu mượn.
UPDATE books SET available_copies = available_copies - 1 
WHERE code = 'B02' AND available_copies > 0;

-- INSERT này vẫn chạy khi UPDATE phía trên ảnh hưởng 0 dòng (sách hết hoặc code không tồn tại),
-- vì transaction không tự hiểu đây là lỗi nghiệp vụ. Kết quả có thể tạo phiếu BORROWED mà tồn kho không bị trừ.
-- Hãy kiểm tra affected row/điều kiện đủ trong cùng statement hoặc dùng khối PL/pgSQL ném exception để rollback.
INSERT INTO borrow_tickets (reader_id, book_id, status)
SELECT r.id, b.id, 'BORROWED' FROM readers r, books b 
WHERE r.code = 'R01' AND b.code = 'B02';

SELECT available_copies FROM books WHERE code = 'B02';
COMMIT;

-- Trả sách có Transaction hoàn chỉnh
BEGIN;
SELECT status FROM borrow_tickets WHERE id = 1 FOR UPDATE;

-- Đang tăng tồn kho trước khi biết UPDATE ticket có chuyển được BORROWED -> RETURNED hay không.
-- Nếu ticket đã RETURNED, câu UPDATE sách vẫn tăng mỗi lần chạy còn UPDATE ticket ảnh hưởng 0 dòng. Hãy chỉ hoàn kho
-- khi transition trạng thái thành công, bảo đảm hai thao tác atomic và kiểm tra không vượt total_copies.
--Trả sách: tăng available_copies và cập nhật phiếu mượn.
UPDATE books SET available_copies = available_copies + 1
WHERE id = (SELECT book_id FROM borrow_tickets WHERE id = 1);

UPDATE borrow_tickets SET status = 'RETURNED', returned_at = CURRENT_TIMESTAMP 
WHERE id = 1 AND status = 'BORROWED';

SELECT * FROM borrow_tickets WHERE id = 1;
COMMIT;
