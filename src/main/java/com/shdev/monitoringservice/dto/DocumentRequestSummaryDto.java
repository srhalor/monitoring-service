package com.shdev.monitoringservice.dto;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO for document request summary statistics.
 * Contains aggregated counts for dashboard display.
 *
 * @param fromDate     Start date of the summary period (optional)
 * @param toDate       End date of the summary period (optional)
 * @param totalCount   Total count of all document requests in the period
 * @param statusCounts List of counts grouped by status
 * @author Shailesh Halor
 */
public record DocumentRequestSummaryDto(
        OffsetDateTime fromDate,
        OffsetDateTime toDate,
        Long totalCount,
        List<StatusCountDto> statusCounts
) {
}

