package com.example.library_borrowing.service.impl;

import com.example.library_borrowing.dto.request.BorrowBookRequest;
import com.example.library_borrowing.dto.request.CreateBookRequest;
import com.example.library_borrowing.dto.request.ReaderCreateRequest;
import com.example.library_borrowing.dto.response.BookResponse;
import com.example.library_borrowing.dto.response.BorrowTicketResponse;
import com.example.library_borrowing.enums.TicketStatus;
import com.example.library_borrowing.exception.ConflictException;
import com.example.library_borrowing.exception.NotFoundException;
import com.example.library_borrowing.model.Book;
import com.example.library_borrowing.model.BorrowTicket;
import com.example.library_borrowing.model.Reader;
import com.example.library_borrowing.service.BorrowBookService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class BorrowServiceImpl implements BorrowBookService {

    private final Map<String, Book> books = new ConcurrentHashMap<>();
    private final Map<String, Reader> readers = new ConcurrentHashMap<>();
    private final Map<Long, BorrowTicket> tickets = new ConcurrentHashMap<>();
    private final AtomicLong ticketIdCounter = new AtomicLong(1);

    @Override
    public BookResponse createBook(CreateBookRequest request) {
        // containsKey() rồi put() là hai thao tác tách rời. ConcurrentHashMap chỉ bảo đảm
        // từng thao tác thread-safe, không làm cả chuỗi check-then-act trở thành atomic. Hai request cùng code vẫn
        // có thể cùng vượt qua kiểm tra và request sau ghi đè request trước. Hãy dùng putIfAbsent/computeIfAbsent
        // và dựa vào giá trị trả về để quyết định ném ConflictException; viết test hai luồng tạo cùng một code.
        if (books.containsKey(request.getCode())){
            throw new ConflictException("da ton tai ma sach nay");
        }
        Book book = new Book();
        book.setCode(request.getCode());
        book.setTitle(request.getTitle());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies());
        book.setActive(true);
        books.put(book.getCode(), book);
        return new BookResponse(book);
    }

    @Override
    public List<BookResponse> searchBooks(String keyword) {
        return books.values().stream()
                .filter(b -> keyword == null || keyword.isBlank() || b.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .map(BookResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponse getBook(String code) {
        Book book = books.get(code);
        if (book == null) throw new NotFoundException("Không tìm thấy sách với mã: " + code);
        return new BookResponse(book);
    }

    @Override
    public void createReader(ReaderCreateRequest request) {
        // Lỗi check-then-act tương tự createBook(): containsKey() + put() không atomic.
        // Hãy sửa bằng API atomic của ConcurrentHashMap và chứng minh bằng test concurrent duplicate readerCode.
        if (readers.containsKey(request.getCode())) {
            throw new ConflictException("Mã độc giả đã tồn tại");
        }
        readers.put(request.getCode(), new Reader(request.getCode(), request.getName()));
    }

    @Override
    public BorrowTicketResponse borrowBook(BorrowBookRequest request) {
        Reader reader = readers.get(request.getReaderCode());
        if (reader == null) throw new NotFoundException("nguoi doc khong ton tai");

        Book book = books.get(request.getBookCode());
        if (book == null) throw new NotFoundException("sach ko ton tai");

        if (!book.isActive()) throw new ConflictException("sach ko kha dung");
        synchronized (book){
            if (book.getAvailableCopies() <= 0) throw new ConflictException("sach da muon het");

            BorrowTicket ticket = new BorrowTicket(
                    ticketIdCounter.getAndIncrement(),
                    reader.getCode(),
                    book.getCode(),
                    TicketStatus.BORROWED,
                    LocalDateTime.now(),
                    null
            );

            tickets.put(ticket.getId(), ticket);
            book.setAvailableCopies(book.getAvailableCopies() - 1);

            return new BorrowTicketResponse(ticket);
        }
    }

    @Override
    public BorrowTicketResponse returnBook(Long ticketId) {
        BorrowTicket ticket = tickets.get(ticketId);
        if (ticket == null) throw new NotFoundException("Khong tim thay phieu muon");

        // Toàn bộ chuỗi kiểm tra BORROWED -> đổi RETURNED -> tăng availableCopies chưa được
        // đồng bộ. Hai request trả cùng ticket có thể cùng thấy BORROWED và cùng tăng tồn kho hai lần. ConcurrentHashMap
        // không bảo vệ state bên trong BorrowTicket/Book. Hãy chọn một lock có ownership rõ ràng, khóa toàn bộ transition
        // và giải thích cách tránh deadlock; sau đó viết test dùng CountDownLatch cho hai request return đồng thời.
        if (ticket.getStatus() == TicketStatus.RETURNED) {
            throw new ConflictException("Phieu nay da duoc tra");
        }

        Book book = books.get(ticket.getBookCode());

        ticket.setStatus(TicketStatus.RETURNED);
        ticket.setReturnedAt(LocalDateTime.now());
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        return new BorrowTicketResponse(ticket);
    }


    @Override
    public BorrowTicketResponse getTicket(Long ticketId) {
        BorrowTicket ticket = tickets.get(ticketId);
        if (ticket == null) throw new NotFoundException("Khong tim thay phieu muon");
        return new BorrowTicketResponse(ticket);
    }

}
