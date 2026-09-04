package com.example.library_borrowing.controller;

import com.example.library_borrowing.dto.request.BorrowBookRequest;
import com.example.library_borrowing.dto.request.CreateBookRequest;
import com.example.library_borrowing.dto.request.ReaderCreateRequest;
import com.example.library_borrowing.dto.response.BookResponse;
import com.example.library_borrowing.dto.response.BorrowTicketResponse;
import com.example.library_borrowing.service.BorrowBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LibraryController {

    private final BorrowBookService borrowBookService;

    @PostMapping("/books")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookRequest request) {
        BookResponse response = borrowBookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @RequestParam(value = "keyword", required = false) String keyword) {
        return ResponseEntity.ok(borrowBookService.searchBooks(keyword));
    }

    @GetMapping("/books/{code}")
    public ResponseEntity<BookResponse> getBookDetails(@PathVariable String code) {
        return ResponseEntity.ok(borrowBookService.getBook(code));
    }

    @PostMapping("/borrows")
    public ResponseEntity<BorrowTicketResponse> borrowBook(@Valid @RequestBody BorrowBookRequest request) {
        BorrowTicketResponse response = borrowBookService.borrowBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Mapping này không có biến {id} nhưng method lại nhận @PathVariable Long id.
    // Request trong api-test.http đang gọi POST /borrows/{id}/return nên endpoint hiện tại không khớp và id
    // không thể được resolve. Yêu cầu sửa mapping cho thống nhất với API contract và tự viết test MockMvc cho route này.
    @PostMapping("/return")
    public ResponseEntity<BorrowTicketResponse> returnBook(@PathVariable Long id) {
        BorrowTicketResponse response = borrowBookService.returnBook(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/borrows/{id}")
    public ResponseEntity<BorrowTicketResponse> getTicketDetails(@PathVariable Long id) {
        return ResponseEntity.ok(borrowBookService.getTicket(id));
    }

    @PostMapping("/readers")
    public ResponseEntity<String> createReader(@Valid @RequestBody ReaderCreateRequest request) {
        borrowBookService.createReader(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Tạo độc giả thành công");
    }


}
