--Thêm sách và độc giả
INSERT INTO books (code, title, total_copies, available_copies, active) VALUES
('B01', 'Java Spring Boot', 5, 5, true),
('B02', 'Clean Code', 3, 3, true),
('B03', 'Design Patterns', 4, 4, true),
('B04', 'Microservices', 2, 2, true),
('B05', 'SQL Basics', 10, 10, true);

INSERT INTO readers (code, name) VALUES
('R01', 'Nguyen Van A'),
('R02', 'Tran Thi B'),
('R03', 'Le Van C');

-- Tạo phiếu mượn
INSERT INTO borrow_tickets (reader_id, book_id, status) VALUES (1, 1, 'BORROWED');
UPDATE books SET available_copies = available_copies - 1 WHERE id = 1;

INSERT INTO borrow_tickets (reader_id, book_id, status) VALUES (1, 2, 'BORROWED');
UPDATE books SET available_copies = available_copies - 1 WHERE id = 2;

INSERT INTO borrow_tickets (reader_id, book_id, status) VALUES (2, 3, 'BORROWED');
UPDATE books SET available_copies = available_copies - 1 WHERE id = 3;

-- Phiếu RETURNED nhưng returned_at lại NULL nên dữ liệu mẫu vi phạm ý nghĩa trạng thái.
-- Bổ sung returned_at và giải thích available_copies của B04 biểu diễn trạng thái hiện tại sau khi sách đã trả.
INSERT INTO borrow_tickets (reader_id, book_id, status) VALUES (2, 4, 'RETURNED');
-- Sách trả lại không cần trừ available_copies

INSERT INTO borrow_tickets (reader_id, book_id, status) VALUES (1, 3, 'BORROWED');
UPDATE books SET available_copies = available_copies - 1 WHERE id = 3;

INSERT INTO borrow_tickets (reader_id, book_id, status) VALUES (2, 1, 'BORROWED');
UPDATE books SET available_copies = available_copies - 1 WHERE id = 1;
