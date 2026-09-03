package com.example.library_borrowing.dto.response;

import com.example.library_borrowing.enums.TicketStatus;
import com.example.library_borrowing.model.BorrowTicket;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BorrowTicketResponse {
    private Long id;
    private String readerCode;
    private String bookCode;
    private TicketStatus status;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;

    public BorrowTicketResponse(BorrowTicket ticket) {
        this.id = ticket.getId();
        this.readerCode = ticket.getReaderCode();
        this.bookCode = ticket.getBookCode();
        this.status = ticket.getStatus();
        this.borrowedAt = ticket.getBorrowedAt();
        this.returnedAt = ticket.getReturnedAt();
    }
}
