package com.shdev.monitoringservice.dto;

import com.shdev.omsdatabase.dto.DocumentRequestOutDto;

import java.util.List;

/**
 * DTO for paginated document request search response.
 * Contains search results, pagination metadata, and navigation links.
 *
 * @param content       List of document requests matching search criteria
 * @param page          Current page number (1-based)
 * @param size          Number of items per page
 * @param totalElements Total number of matching records
 * @param totalPages    Total number of pages
 * @param first         True if this is the first page
 * @param last          True if this is the last page
 * @param sorts         Applied sort criteria
 * @param links         Navigation links
 * @author Shailesh Halor
 */
public record DocumentRequestSearchResponseDto(
        List<DocumentRequestOutDto> content,
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages,
        Boolean first,
        Boolean last,
        List<SortDto> sorts,
        LinksDto links
) {
}

