package com.getset.fullstack.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Quote {
    private String id;
    private String book;
    private String content;
}
