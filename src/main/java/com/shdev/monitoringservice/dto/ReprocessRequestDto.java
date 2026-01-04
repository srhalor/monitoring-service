package com.shdev.monitoringservice.dto;

import java.util.List;

/**
 * DTO for document request reprocessing request.
 * Contains list of request IDs to be reprocessed.
 *
 * @param requestIds List of document request IDs to reprocess (max 100)
 * @author Shailesh Halor
 */
public record ReprocessRequestDto(
        List<Long> requestIds
) {
}

