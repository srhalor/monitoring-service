package com.shdev.monitoringservice.dto;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO for document request search criteria.
 * All filters use AND semantics - results must match all provided criteria.
 *
 * @param sourceSystems     List of reference data IDs for SOURCE_SYSTEM (optional)
 * @param documentTypes     List of reference data IDs for DOCUMENT_TYPE (optional)
 * @param documentNames     List of reference data IDs for DOCUMENT_NAME (optional)
 * @param documentStatuses  List of reference data IDs for DOCUMENT_STATUS (optional)
 * @param requestIds        List of document request IDs (optional, max 100)
 * @param batchIds          List of Thunderhead batch IDs (optional, max 100)
 * @param fromDate          Filter by createdDat >= fromDate (optional)
 * @param toDate            Filter by createdDat <= toDate (optional)
 * @param metadataChips     List of metadata key-value pairs (optional, AND logic)
 * @param sorts             List of sort criteria (optional, default: id DESC)
 * @author Shailesh Halor
 */
public record DocumentRequestSearchRequestDto(
        List<Long> sourceSystems,
        List<Long> documentTypes,
        List<Long> documentNames,
        List<Long> documentStatuses,
        List<Long> requestIds,
        List<Long> batchIds,
        OffsetDateTime fromDate,
        OffsetDateTime toDate,
        List<MetadataChipDto> metadataChips,
        List<SortDto> sorts
) {
}

