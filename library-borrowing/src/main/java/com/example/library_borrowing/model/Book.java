package com.example.library_borrowing.model;

import ch.qos.logback.core.util.DatePatternToRegexUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Book {
    private String code;
    private String title;
    private int totalCopies;
    private int availableCopies;
    private boolean active;

    public Book(String code, String title, int totalCopies, int availableCopies, boolean active) {
        this.code = code;
        this.title = title;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.active = active;
    }

    public Book() {
    }
}
