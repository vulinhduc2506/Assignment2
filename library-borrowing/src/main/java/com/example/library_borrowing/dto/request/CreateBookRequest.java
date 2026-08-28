package com.example.library_borrowing.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookRequest {

    @NotBlank(message = "Mã sách không được để trống")
    private String code;

    @NotBlank(message = "Ten sach khong duwoc trong")
    private String title;

    @NotNull(message = "ton so sach khong duwoc de trong")
    @Min(value = 1, message = "Tổng số sách phải lớn hơn 0")
    private Integer totalCopies;
}
