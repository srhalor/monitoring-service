package com.shdev.monitoringservice.dto;

/**
 * DTO representing a single sort criterion.
 *
 * @param property  Field name to sort by (id, createdDat, lastUpdateDat)
 * @param direction Sort direction (ASC or DESC)
 * @author Shailesh Halor
 */
public record SortDto(
        String property,
        String direction
) {
}

