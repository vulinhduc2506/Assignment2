package com.example.library_borrowing.dto.response;

import com.example.library_borrowing.model.Book;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookResponse {
    private String code;
    private String title;
    private int totalCopies;
    private int availableCopies;
    private boolean active;

    // Hàm chuyển đổi tự động từ Model sang DTO
    public BookResponse(Book book) {
        this.code = book.getCode();
        this.title = book.getTitle();
        this.totalCopies = book.getTotalCopies();
        this.availableCopies = book.getAvailableCopies();
        this.active = book.isActive();
    }
}
