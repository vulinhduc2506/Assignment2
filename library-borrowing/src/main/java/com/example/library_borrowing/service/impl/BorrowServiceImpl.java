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
        // Đã dùng dùng putIfAbsent() – hàm này gộp việc kiểm tra và lưu dữ liệu thành một thao tác nguyên tử
        // (atomic) duy nhất. Nếu Key đã tồn tại, nó sẽ trả về đối tượng cũ (khác null) và không ghi đè.
        Book book = new Book();
        book.setCode(request.getCode());
        book.setTitle(request.getTitle());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies());
        book.setActive(true);

        if (books.putIfAbsent(book.getCode(), book) != null) {
            throw new ConflictException("Da ton tai ma sach nay");
        }
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

        Reader newReader = new Reader(request.getCode(), request.getName());
        if (readers.putIfAbsent(request.getCode(), newReader) != null) {
            throw new ConflictException("Da ton tai ma nguoi doc nay");
        }

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

        //Deadlock chỉ xảy ra nếu luồng A giữ khóa X chờ khóa Y
        //và luồng B giữ khóa Y đòi khóa X
        //Hàm borrowBook khóa book
        //Hàm returnBook phải khóa ticket trước, sau đó mới khóa book
        synchronized (ticket) {
            if (ticket.getStatus() == TicketStatus.RETURNED) {
                throw new ConflictException("Phieu nay da duoc tra");
            }

            ticket.setStatus(TicketStatus.RETURNED);
            ticket.setReturnedAt(LocalDateTime.now());

            Book book = books.get(ticket.getBookCode());
            //Khoa sach cong don ton kho
            synchronized (book) {
                book.setAvailableCopies(book.getAvailableCopies() + 1);
            }

            return new BorrowTicketResponse(ticket);
        }

    }


    @Override
    public BorrowTicketResponse getTicket(Long ticketId) {
        BorrowTicket ticket = tickets.get(ticketId);
        if (ticket == null) throw new NotFoundException("Khong tim thay phieu muon");
        return new BorrowTicketResponse(ticket);
    }

}
