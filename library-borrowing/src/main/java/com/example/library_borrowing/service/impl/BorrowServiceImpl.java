package com.example.library_borrowing.service.impl;

import com.example.library_borrowing.dto.request.BorrowBookRequest;
import com.example.library_borrowing.dto.request.CreateBookRequest;
import com.example.library_borrowing.dto.response.BookResponse;
import com.example.library_borrowing.dto.response.BorrowTicketResponse;
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
import java.util.stream.Collectors;

@Service
public class BorrowServiceImpl implements BorrowBookService {

    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, Reader> readers = new HashMap<>();
    private final Map<Long, BorrowTicket> tickets = new HashMap<Long, BorrowTicket>();

    @Override
    public BookResponse createBook(CreateBookRequest request) {
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

    private Long ticketIdCount;
    @Override
    public BorrowTicketResponse borrowBook(BorrowBookRequest request) {
        Reader reader = readers.get(request.getReaderCode());
        if (reader == null) throw new NotFoundException("nguoi doc khong ton tai");

        Book book = books.get(request.getBookCode());
        if (book == null) throw new NotFoundException("sach ko ton tai");

        if (!book.isActive()) throw new ConflictException("sach ko kha dung");
        if (book.getAvailableCopies() <= 0) throw new ConflictException("sach da muon het");

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        BorrowTicket ticket = new BorrowTicket(
                ticketIdCount++,
                reader.getCode(),
                book.getCode(),
                "BORROWED",
                LocalDateTime.now(),
                null
        );
        tickets.put(ticket.getId(), ticket);
        return new BorrowTicketResponse(ticket);

    }

    @Override
    public BorrowTicketResponse returnBook(Long ticketId) {
        BorrowTicket ticket = tickets.get(ticketId);
        if (ticket == null) throw new NotFoundException("Khong tim thay phieu muon");

        if ("RETURNED".equals(ticket.getStatus())) {
            throw new ConflictException("Phieu nay da duoc tra");
        }

        Book book = books.get(ticket.getBookCode());

        ticket.setStatus("RETURNED");
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
