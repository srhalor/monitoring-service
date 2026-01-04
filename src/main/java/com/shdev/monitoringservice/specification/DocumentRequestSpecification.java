package com.shdev.monitoringservice.specification;

import com.shdev.monitoringservice.dto.DocumentRequestSearchRequestDto;
import com.shdev.monitoringservice.dto.MetadataChipDto;
import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import com.shdev.omsdatabase.entity.RequestsMetadataValueEntity;
import com.shdev.omsdatabase.entity.ThBatchEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification builder for DocumentRequestEntity dynamic queries.
 * Implements complex search criteria with AND semantics.
 *
 * @author Shailesh Halor
 */
public class DocumentRequestSpecification {

    /**
     * Build a composite Specification from search criteria.
     * All filters use AND logic - results must match all provided criteria.
     *
     * @param searchDto Search criteria
     * @return Combined Specification
     */
    public static Specification<DocumentRequestEntity> buildSpecification(DocumentRequestSearchRequestDto searchDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Apply filters if provided
            if (searchDto.sourceSystems() != null && !searchDto.sourceSystems().isEmpty()) {
                predicates.add(hasSourceSystems(searchDto.sourceSystems()).toPredicate(root, query, criteriaBuilder));
            }

            if (searchDto.documentTypes() != null && !searchDto.documentTypes().isEmpty()) {
                predicates.add(hasDocumentTypes(searchDto.documentTypes()).toPredicate(root, query, criteriaBuilder));
            }

            if (searchDto.documentNames() != null && !searchDto.documentNames().isEmpty()) {
                predicates.add(hasDocumentNames(searchDto.documentNames()).toPredicate(root, query, criteriaBuilder));
            }

            if (searchDto.documentStatuses() != null && !searchDto.documentStatuses().isEmpty()) {
                predicates.add(hasDocumentStatuses(searchDto.documentStatuses()).toPredicate(root, query, criteriaBuilder));
            }

            if (searchDto.requestIds() != null && !searchDto.requestIds().isEmpty()) {
                predicates.add(hasRequestIds(searchDto.requestIds()).toPredicate(root, query, criteriaBuilder));
            }

            if (searchDto.batchIds() != null && !searchDto.batchIds().isEmpty()) {
                predicates.add(hasBatchIds(searchDto.batchIds()).toPredicate(root, query, criteriaBuilder));
            }

            if (searchDto.fromDate() != null || searchDto.toDate() != null) {
                predicates.add(hasDateRange(searchDto.fromDate(), searchDto.toDate()).toPredicate(root, query, criteriaBuilder));
            }

            if (searchDto.metadataChips() != null && !searchDto.metadataChips().isEmpty()) {
                predicates.add(hasMetadata(searchDto.metadataChips()).toPredicate(root, query, criteriaBuilder));
            }

            // Add DISTINCT to avoid duplicates from joins
            if (query != null) {
                query.distinct(true);
            }

            // Combine all predicates with AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter by source system reference data IDs.
     */
    private static Specification<DocumentRequestEntity> hasSourceSystems(List<Long> sourceSystemIds) {
        return (root, query, criteriaBuilder) ->
                root.get("omrdaSourceSystem").get("id").in(sourceSystemIds);
    }

    /**
     * Filter by document type reference data IDs.
     */
    private static Specification<DocumentRequestEntity> hasDocumentTypes(List<Long> documentTypeIds) {
        return (root, query, criteriaBuilder) ->
                root.get("omrdaDocumentType").get("id").in(documentTypeIds);
    }

    /**
     * Filter by document name reference data IDs.
     */
    private static Specification<DocumentRequestEntity> hasDocumentNames(List<Long> documentNameIds) {
        return (root, query, criteriaBuilder) ->
                root.get("omrdaDocumentName").get("id").in(documentNameIds);
    }

    /**
     * Filter by document status reference data IDs.
     */
    private static Specification<DocumentRequestEntity> hasDocumentStatuses(List<Long> documentStatusIds) {
        return (root, query, criteriaBuilder) ->
                root.get("omrdaDocStatus").get("id").in(documentStatusIds);
    }

    /**
     * Filter by document request IDs directly.
     */
    private static Specification<DocumentRequestEntity> hasRequestIds(List<Long> requestIds) {
        return (root, query, criteriaBuilder) ->
                root.get("id").in(requestIds);
    }

    /**
     * Filter by Thunderhead batch IDs using EXISTS subquery.
     * Returns requests that have at least one batch with ID in the provided list.
     */
    private static Specification<DocumentRequestEntity> hasBatchIds(List<Long> batchIds) {
        return (root, query, criteriaBuilder) -> {
            if (query == null) {
                return criteriaBuilder.conjunction(); // Return always-true predicate if query is null
            }

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<ThBatchEntity> batchRoot = subquery.from(ThBatchEntity.class);
            subquery.select(batchRoot.get("omdrt").get("id"))
                    .where(
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(batchRoot.get("omdrt").get("id"), root.get("id")),
                                    batchRoot.get("id").in(batchIds)
                            )
                    );
            return criteriaBuilder.exists(subquery);
        };
    }

    /**
     * Filter by date range on createdDat field.
     *
     * @param fromDate Start of date range (inclusive), null = no lower bound
     * @param toDate   End of date range (inclusive), null = no upper bound
     */
    private static Specification<DocumentRequestEntity> hasDateRange(OffsetDateTime fromDate, OffsetDateTime toDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> datePredicates = new ArrayList<>();

            if (fromDate != null) {
                datePredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDat"), fromDate));
            }

            if (toDate != null) {
                datePredicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDat"), toDate));
            }

            return criteriaBuilder.and(datePredicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter by metadata key-value pairs using EXISTS subquery.
     * All metadata chips must match (AND logic).
     *
     * @param metadataChips List of metadata key-value filters
     */
    private static Specification<DocumentRequestEntity> hasMetadata(List<MetadataChipDto> metadataChips) {
        return (root, query, criteriaBuilder) -> {
            if (query == null) {
                return criteriaBuilder.conjunction(); // Return always-true predicate if query is null
            }

            List<Predicate> metadataPredicates = new ArrayList<>();

            // Each metadata chip must have a matching metadata value record
            for (MetadataChipDto chip : metadataChips) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<RequestsMetadataValueEntity> metadataRoot = subquery.from(RequestsMetadataValueEntity.class);
                subquery.select(metadataRoot.get("omdrt").get("id"))
                        .where(
                                criteriaBuilder.and(
                                        criteriaBuilder.equal(metadataRoot.get("omdrt").get("id"), root.get("id")),
                                        criteriaBuilder.equal(metadataRoot.get("omrda").get("id"), chip.keyId()),
                                        criteriaBuilder.equal(metadataRoot.get("metadataValue"), chip.value())
                                )
                        );
                metadataPredicates.add(criteriaBuilder.exists(subquery));
            }

            return criteriaBuilder.and(metadataPredicates.toArray(new Predicate[0]));
        };
    }
}

