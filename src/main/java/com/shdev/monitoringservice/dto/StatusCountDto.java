package com.shdev.monitoringservice.dto;

/**
 * DTO representing count of requests for a specific status.
 *
 * @param statusId          Reference data ID for the status
 * @param statusName        Name/value of the status
 * @param statusDescription Description of the status
 * @param count             Number of requests with this status
 * @author Shailesh Halor
 */
public record StatusCountDto(
        Long statusId,
        String statusName,
        String statusDescription,
        Long count
) {
}

