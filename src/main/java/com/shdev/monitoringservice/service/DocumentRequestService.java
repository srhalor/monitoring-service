package com.shdev.monitoringservice.service;

import com.shdev.monitoringservice.dto.*;
import com.shdev.monitoringservice.exception.InvalidSearchRequestException;
import com.shdev.monitoringservice.exception.ResourceNotFoundException;
import com.shdev.monitoringservice.specification.DocumentRequestSpecification;
import com.shdev.omsdatabase.dto.DocumentContentDto;
import com.shdev.omsdatabase.dto.DocumentRequestOutDto;
import com.shdev.omsdatabase.dto.MetadataValueOutDto;
import com.shdev.omsdatabase.dto.ThBatchOutDto;
import com.shdev.omsdatabase.entity.DocumentRequestBlobEntity;
import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import com.shdev.omsdatabase.entity.RequestsMetadataValueEntity;
import com.shdev.omsdatabase.entity.ThBatchEntity;
import com.shdev.omsdatabase.mapper.DocumentRequestMapper;
import com.shdev.omsdatabase.mapper.MetadataValueMapper;
import com.shdev.omsdatabase.mapper.ThBatchMapper;
import com.shdev.omsdatabase.repository.DocumentRequestBlobEntityRepository;
import com.shdev.omsdatabase.repository.DocumentRequestEntityRepository;
import com.shdev.omsdatabase.repository.RequestsMetadataValueEntityRepository;
import com.shdev.omsdatabase.repository.ThBatchEntityRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for Document Request operations.
 * Provides search functionality with pagination, filtering, and sorting.
 * Also provides detail retrieval for metadata, content, and batches.
 *
 * @author Shailesh Halor
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentRequestService {

    private final DocumentRequestEntityRepository documentRequestRepository;
    private final DocumentRequestMapper documentRequestMapper;
    private final RequestsMetadataValueEntityRepository metadataValueRepository;
    private final MetadataValueMapper metadataValueMapper;
    private final DocumentRequestBlobEntityRepository blobRepository;
    private final ThBatchEntityRepository batchRepository;
    private final ThBatchMapper batchMapper;

    // Validation constants
    private static final int MAX_ID_LIST_SIZE = 100;
    private static final int MAX_PAGE_SIZE = 1000;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final Set<String> VALID_SORT_PROPERTIES = Set.of("id", "createdDat", "lastUpdateDat");

    /**
     * Search document requests with pagination, filtering, and sorting.
     * Uses 1-based page numbering in API (converts to 0-based internally).
     *
     * @param searchRequest Search criteria
     * @param page          Page number (1-based)
     * @param size          Page size
     * @return Paginated search results
     */
    @Transactional(readOnly = true)
    public DocumentRequestSearchResponseDto searchDocumentRequests(
            DocumentRequestSearchRequestDto searchRequest,
            Integer page,
            Integer size) {

        log.info("Searching document requests - page: {}, size: {}", page, size);

        // Validate request
        validateSearchRequest(searchRequest, page, size);

        // Convert 1-based page to 0-based for Spring Data
        int pageIndex = page - 1;

        // Build specification from search criteria
        Specification<DocumentRequestEntity> specification = DocumentRequestSpecification.buildSpecification(searchRequest);

        // Build sort from request or use default
        Sort sort = buildSort(searchRequest.sorts());

        // Create pageable
        Pageable pageable = PageRequest.of(pageIndex, size, sort);

        // Execute query
        Page<DocumentRequestEntity> resultPage = documentRequestRepository.findAll(specification, pageable);

        log.info("Found {} document requests (page {}/{})",
                resultPage.getTotalElements(), page, resultPage.getTotalPages());

        // Map entities to DTOs
        List<DocumentRequestOutDto> content = resultPage.getContent().stream()
                .map(documentRequestMapper::toDto)
                .toList();

        // Build response with 1-based page numbering
        return new DocumentRequestSearchResponseDto(
                content,
                page, // Return 1-based page number
                size,
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                page == 1, // first page
                page >= resultPage.getTotalPages(), // last page
                searchRequest.sorts(),
                buildLinks(page, size, resultPage.getTotalPages())
        );
    }

    /**
     * Validate search request parameters.
     */
    private void validateSearchRequest(DocumentRequestSearchRequestDto request, Integer page, Integer size) {
        // Validate page number
        if (page == null || page < 1) {
            throw new InvalidSearchRequestException("Page number must be >= 1");
        }

        // Validate page size
        if (size == null || size < 1) {
            throw new InvalidSearchRequestException("Page size must be >= 1");
        }

        if (size > MAX_PAGE_SIZE) {
            throw new InvalidSearchRequestException(
                    String.format("Page size must not exceed %d (requested: %d)", MAX_PAGE_SIZE, size));
        }

        // Validate requestIds list size
        if (request.requestIds() != null && request.requestIds().size() > MAX_ID_LIST_SIZE) {
            throw new InvalidSearchRequestException(
                    String.format("requestIds list size must not exceed %d (provided: %d)",
                            MAX_ID_LIST_SIZE, request.requestIds().size()));
        }

        // Validate batchIds list size
        if (request.batchIds() != null && request.batchIds().size() > MAX_ID_LIST_SIZE) {
            throw new InvalidSearchRequestException(
                    String.format("batchIds list size must not exceed %d (provided: %d)",
                            MAX_ID_LIST_SIZE, request.batchIds().size()));
        }

        // Validate sort properties
        if (request.sorts() != null) {
            for (SortDto sortDto : request.sorts()) {
                if (sortDto.property() == null || sortDto.property().isBlank()) {
                    throw new InvalidSearchRequestException("Sort property cannot be null or blank");
                }

                if (!VALID_SORT_PROPERTIES.contains(sortDto.property())) {
                    throw new InvalidSearchRequestException(
                            String.format("Invalid sort property: %s. Valid properties are: %s",
                                    sortDto.property(), VALID_SORT_PROPERTIES));
                }

                if (sortDto.direction() == null || sortDto.direction().isBlank()) {
                    throw new InvalidSearchRequestException("Sort direction cannot be null or blank");
                }

                if (!sortDto.direction().equalsIgnoreCase("ASC") &&
                        !sortDto.direction().equalsIgnoreCase("DESC")) {
                    throw new InvalidSearchRequestException(
                            String.format("Invalid sort direction: %s. Must be ASC or DESC", sortDto.direction()));
                }
            }
        }

        // Validate date range
        if (request.fromDate() != null && request.toDate() != null) {
            if (request.fromDate().isAfter(request.toDate())) {
                throw new InvalidSearchRequestException("fromDate must not be after toDate");
            }
        }
    }

    /**
     * Build Sort object from list of sort DTOs.
     * If no sorts provided, defaults to id DESC.
     */
    private Sort buildSort(List<SortDto> sorts) {
        if (sorts == null || sorts.isEmpty()) {
            // Default sort: id DESC
            return Sort.by(Sort.Direction.DESC, "id");
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (SortDto sortDto : sorts) {
            Sort.Direction direction = Sort.Direction.fromString(sortDto.direction().toUpperCase());
            orders.add(new Sort.Order(direction, sortDto.property()));
        }

        return Sort.by(orders);
    }

    /**
     * Build pagination navigation links (1-based page numbering).
     */
    private LinksDto buildLinks(int currentPage, int size, int totalPages) {
        String baseUrl = "/api/v1/document-requests/search";

        String self = String.format("%s?page=%d&size=%d", baseUrl, currentPage, size);
        String first = String.format("%s?page=1&size=%d", baseUrl, size);
        String last = String.format("%s?page=%d&size=%d", baseUrl, totalPages, size);

        // Previous link (null if on first page)
        String previous = currentPage > 1
                ? String.format("%s?page=%d&size=%d", baseUrl, currentPage - 1, size)
                : null;

        // Next link (null if on last page)
        String next = currentPage < totalPages
                ? String.format("%s?page=%d&size=%d", baseUrl, currentPage + 1, size)
                : null;

        return new LinksDto(self, first, previous, next, last);
    }

    /**
     * Get all metadata values associated with a document request.
     * API 3.2: GET /document-requests/{id}/metadata
     *
     * @param requestId the document request ID
     * @return list of metadata values
     * @throws ResourceNotFoundException if request doesn't exist
     */
    @Transactional(readOnly = true)
    public List<MetadataValueOutDto> getMetadataByRequestId(Long requestId) {
        log.info("Fetching metadata for document request ID: {}", requestId);

        // Verify request exists
        if (!documentRequestRepository.existsById(requestId)) {
            throw new ResourceNotFoundException("Document request not found with ID: " + requestId);
        }

        List<RequestsMetadataValueEntity> metadataEntities = metadataValueRepository.findByOmdrt_Id(requestId);

        log.info("Found {} metadata values for request ID: {}", metadataEntities.size(), requestId);

        return metadataEntities.stream()
                .map(metadataValueMapper::toDto)
                .toList();
    }

    /**
     * Get JSON content for a document request.
     * API 3.3a: GET /document-requests/{id}/json-content
     *
     * @param requestId the document request ID
     * @return document content DTO with JSON content
     * @throws ResourceNotFoundException if request or content doesn't exist
     */
    @Transactional(readOnly = true)
    public DocumentContentDto getJsonContent(Long requestId) {
        log.info("Fetching JSON content for document request ID: {}", requestId);

        // Verify request exists
        if (!documentRequestRepository.existsById(requestId)) {
            throw new ResourceNotFoundException("Document request not found with ID: " + requestId);
        }

        DocumentRequestBlobEntity blobEntity = blobRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No content available for document request ID: " + requestId));

        if (blobEntity.getJsonRequest() == null) {
            throw new ResourceNotFoundException(
                    "JSON content not available for document request ID: " + requestId);
        }

        log.info("Successfully retrieved JSON content for request ID: {}", requestId);

        return new DocumentContentDto(requestId, "JSON", blobEntity.getJsonRequest());
    }

    /**
     * Get XML content for a document request.
     * API 3.3b: GET /document-requests/{id}/xml-content
     *
     * @param requestId the document request ID
     * @return document content DTO with XML content
     * @throws ResourceNotFoundException if request, content, or XML doesn't exist
     */
    @Transactional(readOnly = true)
    public DocumentContentDto getXmlContent(Long requestId) {
        log.info("Fetching XML content for document request ID: {}", requestId);

        // Verify request exists
        if (!documentRequestRepository.existsById(requestId)) {
            throw new ResourceNotFoundException("Document request not found with ID: " + requestId);
        }

        DocumentRequestBlobEntity blobEntity = blobRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No content available for document request ID: " + requestId));

        if (blobEntity.getXmlRequest() == null) {
            throw new ResourceNotFoundException(
                    "XML content not available for document request ID: " + requestId);
        }

        log.info("Successfully retrieved XML content for request ID: {}", requestId);

        return new DocumentContentDto(requestId, "XML", blobEntity.getXmlRequest());
    }

    /**
     * Get all Thunderhead batches associated with a document request.
     * API 3.4: GET /document-requests/{id}/batches
     *
     * @param requestId the document request ID
     * @return list of batches ordered by creation date descending (newest first)
     * @throws ResourceNotFoundException if request doesn't exist
     */
    @Transactional(readOnly = true)
    public List<ThBatchOutDto> getBatchesByRequestId(Long requestId) {
        log.info("Fetching batches for document request ID: {}", requestId);

        // Verify request exists
        if (!documentRequestRepository.existsById(requestId)) {
            throw new ResourceNotFoundException("Document request not found with ID: " + requestId);
        }

        List<ThBatchEntity> batchEntities = batchRepository.findByOmdrt_IdOrderByCreatedDatDesc(requestId);

        log.info("Found {} batches for request ID: {}", batchEntities.size(), requestId);

        return batchEntities.stream()
                .map(batchMapper::toDto)
                .toList();
    }

    /**
     * Initiate reprocessing for document requests.
     * API 3.6: POST /document-requests/reprocess
     *
     * Validates request IDs and returns which ones are valid (submitted) and which are not found.
     * Actual reprocessing functionality is not yet implemented.
     *
     * @param requestIds List of document request IDs to reprocess
     * @return Response indicating submitted and not found request IDs
     */
    @Transactional(readOnly = true)
    public ReprocessResponseDto initiateReprocessing(List<Long> requestIds) {
        log.info("Initiating reprocessing for {} request IDs", requestIds.size());

        // Validate input
        if (requestIds.isEmpty()) {
            throw new InvalidSearchRequestException("Request IDs list cannot be empty");
        }

        if (requestIds.size() > MAX_ID_LIST_SIZE) {
            throw new InvalidSearchRequestException(
                    String.format("Request IDs list size must not exceed %d (provided: %d)",
                            MAX_ID_LIST_SIZE, requestIds.size()));
        }

        // Separate valid and invalid request IDs
        List<Long> submittedIds = new ArrayList<>();
        List<Long> notFoundIds = new ArrayList<>();

        for (Long requestId : requestIds) {
            if (documentRequestRepository.existsById(requestId)) {
                submittedIds.add(requestId);
            } else {
                notFoundIds.add(requestId);
            }
        }

        // TODO: Implement actual reprocessing functionality
        // This will be implemented in a future service that will:
        // 1. Call the reprocessing API endpoint
        // 2. Update request status
        // 3. Create new batch entries if needed
        // 4. Handle reprocessing errors
        // For now, we only validate and return which IDs are valid

        log.info("Reprocessing validation complete: {} submitted, {} not found",
                submittedIds.size(), notFoundIds.size());

        String message = buildReprocessMessage(submittedIds.size(), notFoundIds.size());

        return new ReprocessResponseDto(
                message,
                submittedIds,
                notFoundIds,
                submittedIds.size(),
                notFoundIds.size()
        );
    }

    /**
     * Build message for reprocess response based on submitted and not found counts.
     */
    private String buildReprocessMessage(int submittedCount, int notFoundCount) {
        if (submittedCount == 0 && notFoundCount > 0) {
            return "No valid requests found. All provided request IDs do not exist.";
        } else if (submittedCount > 0 && notFoundCount == 0) {
            return String.format("All %d request(s) submitted for reprocessing.", submittedCount);
        } else {
            return String.format("%d request(s) submitted for reprocessing. %d request(s) not found.",
                    submittedCount, notFoundCount);
        }
    }

    /**
     * Get document request summary statistics for dashboard.
     * API 3.7: GET /document-requests/summary
     *
     * Returns aggregated counts of requests by status within the specified date range.
     *
     * @param fromDate Start date of the summary period (optional)
     * @param toDate   End date of the summary period (optional)
     * @return Summary with total count and counts per status
     */
    @Transactional(readOnly = true)
    public DocumentRequestSummaryDto getRequestSummary(OffsetDateTime fromDate, OffsetDateTime toDate) {
        log.info("Fetching document request summary from {} to {}", fromDate, toDate);

        // Validate date range
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new InvalidSearchRequestException("fromDate must not be after toDate");
        }

        // Build specification for date range filtering
        Specification<DocumentRequestEntity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDat"), fromDate));
            }

            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDat"), toDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // Get all requests in the date range
        List<DocumentRequestEntity> requests = documentRequestRepository.findAll(spec);

        // Calculate total count
        long totalCount = requests.size();

        // Group by status and count
        Map<Long, List<DocumentRequestEntity>> groupedByStatus = requests.stream()
                .collect(Collectors.groupingBy(
                        request -> request.getOmrdaDocStatus().getId()
                ));

        // Build status count DTOs
        List<StatusCountDto> statusCounts = groupedByStatus.entrySet().stream()
                .map(entry -> {
                    Long statusId = entry.getKey();
                    List<DocumentRequestEntity> statusRequests = entry.getValue();
                    DocumentRequestEntity firstRequest = statusRequests.getFirst();

                    return new StatusCountDto(
                            statusId,
                            firstRequest.getOmrdaDocStatus().getRefDataValue(),
                            firstRequest.getOmrdaDocStatus().getDescription(),
                            (long) statusRequests.size()
                    );
                })
                .sorted(Comparator.comparing(StatusCountDto::statusName))
                .toList();

        log.info("Summary calculated: {} total requests, {} unique statuses",
                totalCount, statusCounts.size());

        return new DocumentRequestSummaryDto(
                fromDate,
                toDate,
                totalCount,
                statusCounts
        );
    }
}

