// src/main/java/com/klagan/assetservice/infrastructure/api/dto/PageResponse.java
package com.kLagan.challenge.infraestructure.api.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public static <T, R> PageResponse<R> fromPage(Page<T> page, Function<T, R> mapper) {
        PageResponse<R> response = new PageResponse<>();
        response.setContent(page.getContent().stream().map(mapper).collect(Collectors.toList()));
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        return response;
    }
}