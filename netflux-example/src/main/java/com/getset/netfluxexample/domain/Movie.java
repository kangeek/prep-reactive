package com.getset.netfluxexample.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@AllArgsConstructor
public class Movie {
    private String id;
    @NonNull
    private String title;
}
