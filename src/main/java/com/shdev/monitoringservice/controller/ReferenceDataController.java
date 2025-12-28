package com.shdev.monitoringservice.controller;

import com.shdev.monitoringservice.service.ReferenceDataService;
import com.shdev.omsdatabase.dto.ReferenceDataDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Reference Data operations.
 * Provides endpoints for CRUD operations on reference data.
 * Base path: /api/v1/reference-data
 *
 * @author Shailesh Halor
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/reference-data")
@RequiredArgsConstructor
public class ReferenceDataController {

    private final ReferenceDataService referenceDataService;

    /**
     * Create new reference data.
     * Endpoint: POST /api/v1/reference-data
     *
     * @param dto the reference data DTO
     * @return the created reference data DTO
     */
    @PostMapping
    public ResponseEntity<ReferenceDataDto> createReferenceData(@Valid @RequestBody ReferenceDataDto dto) {
        log.info("Received request to create reference data: type={}, value={}", dto.refDataType(), dto.refDataValue());
        ReferenceDataDto created = referenceDataService.createReferenceData(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update existing reference data by ID.
     * Endpoint: PUT /api/v1/reference-data/{id}
     *
     * @param id  the reference data ID
     * @param dto the reference data DTO with updated values
     * @return the updated reference data DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReferenceDataDto> updateReferenceData(
            @PathVariable Long id,
            @Valid @RequestBody ReferenceDataDto dto) {
        log.info("Received request to update reference data with ID: {}", id);
        ReferenceDataDto updated = referenceDataService.updateReferenceData(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete reference data by ID (logical delete).
     * Endpoint: DELETE /api/v1/reference-data/{id}
     *
     * @param id the reference data ID
     * @return 204 No Content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReferenceData(@PathVariable Long id) {
        log.info("Received request to delete reference data with ID: {}", id);
        referenceDataService.deleteReferenceData(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get reference data by ID.
     * Endpoint: GET /api/v1/reference-data/{id}
     *
     * @param id the reference data ID
     * @return the reference data DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReferenceDataDto> getReferenceDataById(@PathVariable Long id) {
        log.info("Received request to get reference data with ID: {}", id);
        ReferenceDataDto dto = referenceDataService.getReferenceDataById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Get all reference data.
     * Endpoint: GET /api/v1/reference-data
     * Query params: ?historic=true (optional, default=false)
     * Note: Full set is returned; UI handles pagination, sorting, searching client-side.
     *
     * @param historic if true, returns all records including historical versions; if false (default), returns only active records
     * @return list of reference data DTOs
     */
    @GetMapping
    public ResponseEntity<List<ReferenceDataDto>> getAllReferenceData(
            @RequestParam(required = false, defaultValue = "false") boolean historic) {
        log.info("Received request to get all reference data (historic={})", historic);
        List<ReferenceDataDto> dtos = referenceDataService.getAllReferenceData(historic);
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get reference data by type.
     * Endpoint: GET /api/v1/reference-data/type/{type}
     * Query params: ?historic=true (optional, default=false)
     * Note: Full set is returned; UI handles pagination, sorting, searching client-side.
     *
     * @param type     the reference data type
     * @param historic if true, returns all records including historical versions; if false (default), returns only active records
     * @return list of reference data DTOs matching the type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ReferenceDataDto>> getReferenceDataByType(
            @PathVariable String type,
            @RequestParam(required = false, defaultValue = "false") boolean historic) {
        log.info("Received request to get reference data by type: {} (historic={})", type, historic);
        List<ReferenceDataDto> dtos = referenceDataService.getReferenceDataByType(type, historic);
        return ResponseEntity.ok(dtos);
    }
}

