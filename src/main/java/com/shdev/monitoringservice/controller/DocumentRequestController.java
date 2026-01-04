package com.shdev.monitoringservice.controller;

import com.shdev.monitoringservice.dto.*;
import com.shdev.monitoringservice.service.DocumentRequestService;
import com.shdev.omsdatabase.dto.DocumentContentDto;
import com.shdev.omsdatabase.dto.MetadataValueOutDto;
import com.shdev.omsdatabase.dto.ThBatchOutDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * REST controller for Document Request operations.
 * Provides endpoints for searching document requests with advanced filtering
 * and retrieving detailed information (metadata, content, batches).
 *
 * @author Shailesh Halor
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/document-requests")
@RequiredArgsConstructor
public class DocumentRequestController {

    private final DocumentRequestService documentRequestService;

    /**
     * Search document requests with pagination, filtering, and sorting.
     * Uses POST method to support complex search criteria in request body.
     * Page numbering is 1-based (first page = 1).
     *
     * @param page          Page number (1-based, default: 1)
     * @param size          Page size (default: 10)
     * @param searchRequest Search criteria and filters
     * @return Paginated search results
     */
    @PostMapping("/search")
    public ResponseEntity<DocumentRequestSearchResponseDto> searchDocumentRequests(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestBody DocumentRequestSearchRequestDto searchRequest) {

        log.info("Received search request - page: {}, size: {}", page, size);

        DocumentRequestSearchResponseDto response = documentRequestService.searchDocumentRequests(
                searchRequest, page, size);

        log.info("Returning {} results", response.content().size());

        return ResponseEntity.ok(response);
    }

    /**
     * Get all metadata values for a specific document request.
     * API 3.2: Returns list of metadata key-value pairs.
     *
     * @param id the document request ID
     * @return list of metadata values (empty list if no metadata)
     */
    @GetMapping("/{id}/metadata")
    public ResponseEntity<List<MetadataValueOutDto>> getMetadata(@PathVariable Long id) {
        log.info("Received request to get metadata for document request ID: {}", id);

        List<MetadataValueOutDto> metadata = documentRequestService.getMetadataByRequestId(id);

        log.info("Returning {} metadata values for request ID: {}", metadata.size(), id);

        return ResponseEntity.ok(metadata);
    }

    /**
     * Get JSON content for a specific document request.
     * API 3.3a: Returns the original JSON request payload.
     *
     * @param id the document request ID
     * @return document content with JSON payload
     */
    @GetMapping("/{id}/json-content")
    public ResponseEntity<DocumentContentDto> getJsonContent(@PathVariable Long id) {
        log.info("Received request to get JSON content for document request ID: {}", id);

        DocumentContentDto content = documentRequestService.getJsonContent(id);

        log.info("Successfully retrieved JSON content for request ID: {}", id);

        return ResponseEntity.ok(content);
    }

    /**
     * Get XML content for a specific document request.
     * API 3.3b: Returns the final XML request payload.
     *
     * @param id the document request ID
     * @return document content with XML payload
     */
    @GetMapping("/{id}/xml-content")
    public ResponseEntity<DocumentContentDto> getXmlContent(@PathVariable Long id) {
        log.info("Received request to get XML content for document request ID: {}", id);

        DocumentContentDto content = documentRequestService.getXmlContent(id);

        log.info("Successfully retrieved XML content for request ID: {}", id);

        return ResponseEntity.ok(content);
    }

    /**
     * Get all Thunderhead batches for a specific document request.
     * API 3.4: Returns list of batches ordered by creation date (newest first).
     *
     * @param id the document request ID
     * @return list of batches (empty list if no batches)
     */
    @GetMapping("/{id}/batches")
    public ResponseEntity<List<ThBatchOutDto>> getBatches(@PathVariable Long id) {
        log.info("Received request to get batches for document request ID: {}", id);

        List<ThBatchOutDto> batches = documentRequestService.getBatchesByRequestId(id);

        log.info("Returning {} batches for request ID: {}", batches.size(), id);

        return ResponseEntity.ok(batches);
    }

    /**
     * Initiate reprocessing for document requests.
     * API 3.6: Validates request IDs and submits them for reprocessing.
     *
     * Note: Actual reprocessing functionality is not yet implemented.
     * This endpoint only validates the request IDs and returns which ones are valid.
     *
     * @param reprocessRequest Request containing list of document request IDs
     * @return Response indicating submitted and not found request IDs
     */
    @PostMapping("/reprocess")
    public ResponseEntity<ReprocessResponseDto> initiateReprocessing(
            @RequestBody ReprocessRequestDto reprocessRequest) {

        log.info("Received reprocess request for {} request IDs",
                reprocessRequest.requestIds() != null ? reprocessRequest.requestIds().size() : 0);

        ReprocessResponseDto response = documentRequestService.initiateReprocessing(
                reprocessRequest.requestIds() != null ? reprocessRequest.requestIds() : List.of());

        log.info("Reprocess response: {} submitted, {} not found",
                response.totalSubmitted(), response.totalNotFound());

        return ResponseEntity.ok(response);
    }

    /**
     * Get document request summary statistics for dashboard.
     * API 3.7: Returns aggregated counts by status within date range.
     *
     * @param fromDate Start date of the summary period (optional, format: ISO 8601)
     * @param toDate   End date of the summary period (optional, format: ISO 8601)
     * @return Summary with total count and counts per status
     */
    @GetMapping("/summary")
    public ResponseEntity<DocumentRequestSummaryDto> getRequestSummary(
            @RequestParam(required = false) OffsetDateTime fromDate,
            @RequestParam(required = false) OffsetDateTime toDate) {

        log.info("Received request for summary from {} to {}", fromDate, toDate);

        DocumentRequestSummaryDto summary = documentRequestService.getRequestSummary(fromDate, toDate);

        log.info("Returning summary with {} total requests and {} status groups",
                summary.totalCount(), summary.statusCounts().size());

        return ResponseEntity.ok(summary);
    }
}

