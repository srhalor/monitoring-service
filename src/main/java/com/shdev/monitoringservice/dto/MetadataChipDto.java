package com.shdev.monitoringservice.dto;

/**
 * DTO representing a metadata key-value filter criterion.
 * Used to filter document requests by specific metadata values.
 *
 * @param keyId Reference data ID for the metadata key
 * @param value The metadata value to search for
 * @author Shailesh Halor
 */
public record MetadataChipDto(
        Long keyId,
        String value
) {
}

