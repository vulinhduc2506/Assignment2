package com.example.library_borrowing.dto.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class ReaderCreateRequest {
    @NotBlank(message = "Mã độc giả không trống")
    private String code;
    @NotBlank(message = "Tên độc giả không trống")
    private String name;
}
