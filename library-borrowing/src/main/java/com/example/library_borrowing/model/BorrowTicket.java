package com.example.library_borrowing.model;

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
    private String status;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;
}
