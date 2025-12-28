package com.shdev.monitoringservice.controller;

import com.shdev.omsdatabase.dto.DocumentConfigInDto;
import com.shdev.omsdatabase.dto.DocumentConfigOutDto;
import com.shdev.monitoringservice.service.DocumentConfigurationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Document Configuration operations.
 * Provides endpoints for CRUD operations and searching document configurations.
 *
 * @author Shailesh Halor
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/document-configurations")
@RequiredArgsConstructor
public class DocumentConfigurationController {

    private final DocumentConfigurationService documentConfigurationService;

    /**
     * Create new document configuration.
     * POST /api/v1/document-configurations
     *
     * @param dto the document configuration input DTO
     * @return the created document configuration output DTO with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<DocumentConfigOutDto> createDocumentConfiguration(
            @Valid @RequestBody DocumentConfigInDto dto) {
        log.info("Received request to create document configuration");

        DocumentConfigOutDto created = documentConfigurationService.createDocumentConfiguration(dto);

        log.info("Successfully created document configuration with ID: {}", created.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update existing document configuration by ID.
     * PUT /api/v1/document-configurations/{id}
     *
     * @param id  the document configuration ID
     * @param dto the document configuration input DTO with updated values
     * @return the updated document configuration output DTO with HTTP 200 status
     */
    @PutMapping("/{id}")
    public ResponseEntity<DocumentConfigOutDto> updateDocumentConfiguration(
            @PathVariable Long id,
            @Valid @RequestBody DocumentConfigInDto dto) {
        log.info("Received request to update document configuration with ID: {}", id);

        DocumentConfigOutDto updated = documentConfigurationService.updateDocumentConfiguration(id, dto);

        log.info("Successfully updated document configuration with ID: {}", id);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete document configuration by ID (logical delete).
     * DELETE /api/v1/document-configurations/{id}
     *
     * @param id the document configuration ID
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentConfiguration(@PathVariable Long id) {
        log.info("Received request to delete document configuration with ID: {}", id);

        documentConfigurationService.deleteDocumentConfiguration(id);

        log.info("Successfully deleted document configuration with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get document configuration by ID.
     * GET /api/v1/document-configurations/{id}
     *
     * @param id the document configuration ID
     * @return the document configuration output DTO with HTTP 200 status
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentConfigOutDto> getDocumentConfigurationById(@PathVariable Long id) {
        log.info("Received request to get document configuration with ID: {}", id);

        DocumentConfigOutDto config = documentConfigurationService.getDocumentConfigurationById(id);

        return ResponseEntity.ok(config);
    }

    /**
     * Get all document configurations.
     * GET /api/v1/document-configurations
     * Query params: ?historic=true (optional, default=false)
     *
     * @param historic if true, returns all records including historical versions; if false (default), returns only active records
     * @return list of all document configuration output DTOs with HTTP 200 status
     */
    @GetMapping
    public ResponseEntity<List<DocumentConfigOutDto>> getAllDocumentConfigurations(
            @RequestParam(required = false, defaultValue = "false") boolean historic) {
        log.info("Received request to get all document configurations (historic={})", historic);

        List<DocumentConfigOutDto> configs = documentConfigurationService.getAllDocumentConfigurations(historic);

        log.info("Returning {} {} document configurations", configs.size(), historic ? "" : "active");
        return ResponseEntity.ok(configs);
    }

    /**
     * Search document configurations by footer, document name, and code.
     * GET /api/v1/document-configurations/search?footer=0&documentName=IVBRKCOM&code=SIGNEE_1&historic=true
     *
     * @param footer       the footer value (e.g., "0", "1")
     * @param documentName the document name (e.g., "IVBRKCOM", "POSHOOFF")
     * @param code         the configuration code (e.g., "SIGNEE_1", "SIGNEE_2")
     * @param historic     if true, returns all records including historical versions; if false (default), returns only active records
     * @return list of matching document configuration output DTOs with HTTP 200 status
     */
    @GetMapping("/search")
    public ResponseEntity<List<DocumentConfigOutDto>> searchDocumentConfigurations(
            @RequestParam String footer,
            @RequestParam String documentName,
            @RequestParam String code,
            @RequestParam(required = false, defaultValue = "false") boolean historic) {
        log.info("Received search request: footer={}, documentName={}, code={} (historic={})",
                footer, documentName, code, historic);

        List<DocumentConfigOutDto> configs = documentConfigurationService
                .searchDocumentConfigurations(footer, documentName, code, historic);

        log.info("Search returned {} {} document configurations", configs.size(), historic ? "" : "active");
        return ResponseEntity.ok(configs);
    }
}

