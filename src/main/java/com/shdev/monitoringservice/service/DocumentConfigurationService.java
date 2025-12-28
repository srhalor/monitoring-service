package com.shdev.monitoringservice.service;

import com.shdev.monitoringservice.exception.ResourceNotFoundException;
import com.shdev.omsdatabase.dto.DocumentConfigInDto;
import com.shdev.omsdatabase.dto.DocumentConfigOutDto;
import com.shdev.omsdatabase.entity.DocumentConfigEntity;
import com.shdev.omsdatabase.mapper.DocumentConfigurationMapper;
import com.shdev.omsdatabase.repository.DocumentConfigEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Service class for Document Configuration operations.
 * Provides CRUD operations for document configuration management.
 *
 * @author Shailesh Halor
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentConfigurationService {

    private final DocumentConfigEntityRepository documentConfigRepository;
    private final DocumentConfigurationMapper documentConfigMapper;

    /**
     * Create new document configuration.
     *
     * @param dto the document configuration input DTO
     * @return the created document configuration output DTO
     */
    @Transactional
    public DocumentConfigOutDto createDocumentConfiguration(DocumentConfigInDto dto) {
        log.info("Creating document configuration: footerId={}, appDocSpecId={}, codeId={}, value={}",
                dto.footerId(), dto.appDocSpecId(), dto.codeId(), dto.value());

        DocumentConfigEntity entity = documentConfigMapper.toEntity(dto);
        DocumentConfigEntity savedEntity = documentConfigRepository.save(entity);

        log.info("Successfully created document configuration with ID: {}", savedEntity.getId());
        return documentConfigMapper.toDto(savedEntity);
    }

    /**
     * Update existing document configuration by ID.
     * The database trigger automatically handles versioning by:
     * 1. Closing the current version (sets effect_to_dat to SYSTIMESTAMP - 1 second)
     * 2. Inserting a new version with updated values
     * <p>
     * This method refreshes to return the newly created active version.
     *
     * @param id  the document configuration ID
     * @param dto the document configuration input DTO with updated values
     * @return the newly created document configuration output DTO (with new ID)
     */
    @Transactional
    public DocumentConfigOutDto updateDocumentConfiguration(Long id, DocumentConfigInDto dto) {
        log.info("Updating document configuration with ID: {}", id);

        DocumentConfigEntity entity = findByIdOrThrow(id);

        // Capture business key for finding active version after update
        Long footerId = entity.getOmrdaFooter().getId();
        Long appDocSpecId = entity.getOmrdaAppDocSpec().getId();
        Long codeId = entity.getOmrdaCode().getId();
        String value = entity.getValue();

        // Simple update - trigger handles versioning automatically
        documentConfigMapper.updateEntity(dto, entity);
        documentConfigRepository.saveAndFlush(entity);

        // Find the newly created active version - query directly from database
        List<DocumentConfigEntity> activeVersions = documentConfigRepository
                .findByBusinessKeyAndActive(footerId, appDocSpecId, codeId, value, OffsetDateTime.now());

        if (!activeVersions.isEmpty()) {
            log.info("Successfully updated document configuration. Old ID: {}, New ID: {}",
                    id, activeVersions.getFirst().getId());
            return documentConfigMapper.toDto(activeVersions.getFirst());
        }

        log.warn("No active version found after update. Returning closed version.");
        return documentConfigMapper.toDto(entity);
    }

    /**
     * Delete document configuration by ID (logical delete).
     * Sets effect_to_dat to current timestamp - 1 second to make the record inactive.
     *
     * @param id the document configuration ID
     */
    @Transactional
    public void deleteDocumentConfiguration(Long id) {
        log.info("Deleting document configuration with ID: {} (logical delete)", id);

        DocumentConfigEntity entity = findByIdOrThrow(id);
        entity.setEffectToDat(OffsetDateTime.now().minusSeconds(1));
        documentConfigRepository.save(entity);

        log.info("Successfully performed logical delete for document configuration with ID: {}", id);
    }

    /**
     * Get document configuration by ID.
     *
     * @param id the document configuration ID
     * @return the document configuration output DTO
     */
    @Transactional(readOnly = true)
    public DocumentConfigOutDto getDocumentConfigurationById(Long id) {
        log.info("Fetching document configuration with ID: {}", id);
        return documentConfigMapper.toDto(findByIdOrThrow(id));
    }

    /**
     * Get all document configurations.
     *
     * @param historic if true, returns all records including historical versions; if false, returns only active records
     * @return list of all document configuration output DTOs
     */
    @Transactional(readOnly = true)
    public List<DocumentConfigOutDto> getAllDocumentConfigurations(boolean historic) {
        log.info("Fetching all document configurations (historic={})", historic);

        List<DocumentConfigEntity> entities = historic
                ? documentConfigRepository.findAll()
                : documentConfigRepository.findAllActive(OffsetDateTime.now());

        log.info("Found {} {} document configuration records", entities.size(), historic ? "" : "active");
        return entities.stream()
                .map(documentConfigMapper::toDto)
                .toList();
    }

    /**
     * Search document configurations by footer, document name, and code.
     *
     * @param footerValue    the footer value (e.g., "0", "1")
     * @param documentName   the document name (e.g., "IVBRKCOM", "POSHOOFF")
     * @param code          the configuration code (e.g., "SIGNEE_1", "SIGNEE_2")
     * @param historic      if true, returns all records including historical versions; if false, returns only active records
     * @return list of matching document configuration output DTOs
     */
    @Transactional(readOnly = true)
    public List<DocumentConfigOutDto> searchDocumentConfigurations(
            String footerValue, String documentName, String code, boolean historic) {
        log.info("Searching document configurations: footer={}, documentName={}, code={} (historic={})",
                footerValue, documentName, code, historic);

        List<DocumentConfigEntity> entities = historic
                ? documentConfigRepository.findByFooterAndDocumentNameAndCode(footerValue, documentName, code)
                : documentConfigRepository.findByFooterAndDocumentNameAndCodeActive(footerValue, documentName, code, OffsetDateTime.now());

        if (entities.isEmpty()) {
            log.warn("No {} document configurations found for: footer={}, documentName={}, code={}",
                    historic ? "" : "active", footerValue, documentName, code);
        } else {
            log.info("Found {} {} document configuration(s)", entities.size(), historic ? "" : "active");
        }

        return entities.stream()
                .map(documentConfigMapper::toDto)
                .toList();
    }

    /**
     * Helper method to find entity by ID or throw exception.
     *
     * @param id the document configuration ID
     * @return the document configuration entity
     * @throws ResourceNotFoundException if not found
     */
    private DocumentConfigEntity findByIdOrThrow(Long id) {
        return documentConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document configuration not found with ID: " + id));
    }
}

