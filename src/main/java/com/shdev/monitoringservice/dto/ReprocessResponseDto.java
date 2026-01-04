package com.shdev.monitoringservice.dto;

import java.util.List;

/**
 * DTO for document request reprocessing response.
 * Contains information about successful and failed reprocessing requests.
 *
 * @param message              Overall message about the reprocessing request
 * @param submittedRequestIds  List of request IDs that were submitted for reprocessing
 * @param notFoundRequestIds   List of request IDs that were not found in the system
 * @param totalSubmitted       Total count of requests submitted for reprocessing
 * @param totalNotFound        Total count of requests not found
 * @author Shailesh Halor
 */
public record ReprocessResponseDto(
        String message,
        List<Long> submittedRequestIds,
        List<Long> notFoundRequestIds,
        Integer totalSubmitted,
        Integer totalNotFound
) {
}

