package com.shdev.monitoringservice.service;

import com.shdev.monitoringservice.exception.ResourceNotFoundException;
import com.shdev.omsdatabase.dto.ReferenceDataDto;
import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import com.shdev.omsdatabase.mapper.ReferenceDataMapper;
import com.shdev.omsdatabase.repository.ReferenceDataEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Service class for Reference Data operations.
 * Provides CRUD operations for reference data management.
 *
 * @author Shailesh Halor
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReferenceDataService {

    private final ReferenceDataEntityRepository referenceDataRepository;
    private final ReferenceDataMapper referenceDataMapper;

    /**
     * Create new reference data.
     *
     * @param dto the reference data DTO
     * @return the created reference data DTO
     */
    @Transactional
    public ReferenceDataDto createReferenceData(ReferenceDataDto dto) {
        log.info("Creating reference data with type: {}, value: {}", dto.refDataType(), dto.refDataValue());

        ReferenceDataEntity savedEntity = referenceDataRepository.save(referenceDataMapper.toEntity(dto));

        log.info("Successfully created reference data with ID: {}", savedEntity.getId());
        return referenceDataMapper.toDto(savedEntity);
    }

    /**
     * Update existing reference data by ID.
     * The database trigger automatically handles versioning by:
     * 1. Closing the current version (sets effect_to_dat to yesterday)
     * 2. Inserting a new version with updated values
     * <p>
     * This method refreshes to return the newly created active version.
     *
     * @param id  the reference data ID
     * @param dto the reference data DTO with updated values
     * @return the newly created reference data DTO (with new ID)
     */
    @Transactional
    public ReferenceDataDto updateReferenceData(Long id, ReferenceDataDto dto) {
        log.info("Updating reference data with ID: {}", id);

        ReferenceDataEntity entity = findByIdOrThrow(id);
        String refDataType = entity.getRefDataType();
        String refDataValue = entity.getRefDataValue();

        // Simple update - trigger handles versioning automatically
        referenceDataMapper.updateEntity(dto, entity);
        referenceDataRepository.saveAndFlush(entity);

        // Find the newly created active version - query directly from database
        List<ReferenceDataEntity> activeVersions = referenceDataRepository
                .findByRefDataTypeAndValueAndActive(refDataType, refDataValue, OffsetDateTime.now());

        if (!activeVersions.isEmpty()) {
            log.info("Successfully updated reference data. Old ID: {}, New ID: {}", id, activeVersions.getFirst().getId());
            return referenceDataMapper.toDto(activeVersions.getFirst());
        }

        log.warn("No active version found after update. Returning closed version.");
        return referenceDataMapper.toDto(entity);
    }

    /**
     * Delete reference data by ID (logical delete).
     * Sets effect_to_dat to current timestamp - 1 second to make the record inactive.
     *
     * @param id the reference data ID
     */
    @Transactional
    public void deleteReferenceData(Long id) {
        log.info("Deleting reference data with ID: {} (logical delete)", id);

        ReferenceDataEntity entity = findByIdOrThrow(id);
        entity.setEffectToDat(OffsetDateTime.now().minusSeconds(1));
        referenceDataRepository.save(entity);

        log.info("Successfully performed logical delete for reference data with ID: {}", id);
    }

    /**
     * Get reference data by ID.
     *
     * @param id the reference data ID
     * @return the reference data DTO
     */
    @Transactional(readOnly = true)
    public ReferenceDataDto getReferenceDataById(Long id) {
        log.info("Fetching reference data with ID: {}", id);
        return referenceDataMapper.toDto(findByIdOrThrow(id));
    }

    /**
     * Get all reference data.
     *
     * @param historic if true, returns all records including historical versions; if false, returns only active records
     * @return list of all reference data DTOs
     */
    @Transactional(readOnly = true)
    public List<ReferenceDataDto> getAllReferenceData(boolean historic) {
        log.info("Fetching all reference data (historic={})", historic);

        List<ReferenceDataEntity> entities = historic
                ? referenceDataRepository.findAll()
                : referenceDataRepository.findAllActive(OffsetDateTime.now());

        log.info("Found {} {} reference data records", entities.size(), historic ? "" : "active");
        return mapToDtos(entities);
    }

    /**
     * Get reference data by type.
     *
     * @param type     the reference data type
     * @param historic if true, returns all records including historical versions; if false, returns only active records
     * @return list of reference data DTOs matching the type
     */
    @Transactional(readOnly = true)
    public List<ReferenceDataDto> getReferenceDataByType(String type, boolean historic) {
        log.info("Fetching reference data by type: {} (historic={})", type, historic);

        List<ReferenceDataEntity> entities = historic
                ? referenceDataRepository.findByRefDataType(type)
                : referenceDataRepository.findByRefDataTypeAndActive(type, OffsetDateTime.now());

        if (entities.isEmpty()) {
            throw new ResourceNotFoundException(
                    (historic ? "No" : "No active") + " reference data found for type: " + type
            );
        }

        log.info("Found {} reference data records for type: {}", entities.size(), type);
        return mapToDtos(entities);
    }

    // ========== Private Helper Methods ==========

    /**
     * Find entity by ID or throw ResourceNotFoundException.
     */
    private ReferenceDataEntity findByIdOrThrow(Long id) {
        return referenceDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reference data", id));
    }


    /**
     * Map entities to DTOs.
     */
    private List<ReferenceDataDto> mapToDtos(List<ReferenceDataEntity> entities) {
        return entities.stream()
                .map(referenceDataMapper::toDto)
                .toList();
    }
}
