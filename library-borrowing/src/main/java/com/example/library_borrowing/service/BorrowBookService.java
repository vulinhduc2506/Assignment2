package com.example.library_borrowing.service;

import com.example.library_borrowing.dto.request.BorrowBookRequest;
import com.example.library_borrowing.dto.request.CreateBookRequest;
import com.example.library_borrowing.dto.response.BookResponse;
import com.example.library_borrowing.dto.response.BorrowTicketResponse;

import java.util.List;

public interface BorrowBookService {
    BookResponse createBook(CreateBookRequest request);
    List<BookResponse> searchBooks(String keyword);
    BookResponse getBook(String code);

    BorrowTicketResponse borrowBook(BorrowBookRequest request);
    BorrowTicketResponse returnBook(Long ticketId);
    BorrowTicketResponse getTicket(Long ticketId);
}
