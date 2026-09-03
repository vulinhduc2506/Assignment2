package com.example.library_borrowing.model;

import com.example.library_borrowing.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BorrowTicket {
    private Long id;
    private String readerCode;
    private String bookCode;
    private TicketStatus status;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;

    public BorrowTicket(Long id, String readerCode, String bookCode, TicketStatus status, LocalDateTime borrowedAt, LocalDateTime returnedAt) {
        this.id = id;
        this.readerCode = readerCode;
        this.bookCode = bookCode;
        this.status = status;
        this.borrowedAt = borrowedAt;
        this.returnedAt = returnedAt;
    }

    public BorrowTicket() {
    }
}
