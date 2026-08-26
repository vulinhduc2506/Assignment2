-- Q-01: Sách active và còn để mượn
SELECT * FROM books WHERE active = true AND available_copies > 0;

-- Q-02: Tìm sách theo tiêu đề, không phân biệt hoa thường
SELECT * FROM books WHERE ILIKE '%java%';

-- Q-03: Hiển thị phiếu mượn kèm tên độc giả và sách
SELECT t.id, r.name AS reader_name, b.title AS book_title, t.status, t.borrowed_at
FROM borrow_tickets t
JOIN readers r ON t.reader_id = r.id
JOIN books b ON t.book_id = b.id;

-- Q-04: Thống kê phiếu mượn theo từng sách (có cả sách chưa mượn)
SELECT b.code, b.title, COUNT(t.id) as total_borrows
FROM books b
LEFT JOIN borrow_tickets t ON b.id = t.book_id
GROUP BY b.id, b.code, b.title;

-- Q-05: Sách chưa từng được mượn
SELECT b.* FROM books b
LEFT JOIN borrow_tickets t ON b.id = t.book_id
WHERE t.id IS NULL;

-- Q-06: Độc giả hiện không có phiếu BORROWED
SELECT r.* FROM readers r
LEFT JOIN borrow_tickets t ON r.id = t.reader_id AND t.status = 'BORROWED'
WHERE t.id IS NULL;

-- Q-07: Sách có ít nhất 2 phiếu mượn dùng GROUP BY và HAVING
SELECT b.code, b.title, COUNT(t.id) as total_borrows
FROM books b
JOIN borrow_tickets t ON b.id = t.book_id
GROUP BY b.id, b.code, b.title
HAVING COUNT(t.id) >= 2;

-- Q-08: Sách có số phiếu mượn lớn hơn mức trung bình của tất cả sách
WITH BookBorrows AS (
    SELECT book_id, COUNT(id) as cnt FROM borrow_tickets GROUP BY book_id
)
SELECT b.code, b.title
FROM books b
JOIN BookBorrows bb ON b.id = bb.book_id
WHERE bb.cnt > (SELECT AVG(cnt) FROM BookBorrows);
