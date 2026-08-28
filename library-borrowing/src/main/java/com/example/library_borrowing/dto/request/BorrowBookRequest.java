package com.example.library_borrowing.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BorrowBookRequest {
    @NotBlank(message = "Mã độc giả không được để trống")
    private String readerCode;

    @NotBlank(message = "Mã sách không được để trống")
    private String bookCode;
}
