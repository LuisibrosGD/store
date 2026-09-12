package com.example.store.common;

import org.springframework.data.domain.Page;
import java.util.List;

public class PaginaResponse<T> {
    private final List<T> content;
    private final long totalElements;
    private final int totalPages;
    private final int currentPage;
    private final int size;

    public PaginaResponse(Page<T> page, int currentPage, int size) {
        this.content = page.getContent();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.currentPage = currentPage;
        this.size = size;
    }

    public List<T> getContent() { return content; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public int getCurrentPage() { return currentPage; }
    public int getSize() { return size; }
}
