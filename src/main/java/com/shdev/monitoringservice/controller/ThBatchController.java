package com.shdev.monitoringservice.controller;

import com.shdev.monitoringservice.service.ThBatchService;
import com.shdev.omsdatabase.dto.ErrorDetailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Thunderhead Batch operations.
 * Provides endpoints for retrieving batch-related information.
 *
 * @author Shailesh Halor
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/batches")
@RequiredArgsConstructor
public class ThBatchController {

    private final ThBatchService batchService;

    /**
     * Get all error details for a specific batch.
     * API 3.5: Returns list of errors that occurred during batch processing.
     *
     * @param batchId the batch ID
     * @return list of error details (empty list if no errors - successful batch)
     */
    @GetMapping("/{batchId}/errors")
    public ResponseEntity<List<ErrorDetailDto>> getErrors(@PathVariable Long batchId) {
        log.info("Received request to get errors for batch ID: {}", batchId);

        List<ErrorDetailDto> errors = batchService.getErrorsByBatchId(batchId);

        log.info("Returning {} errors for batch ID: {}", errors.size(), batchId);

        return ResponseEntity.ok(errors);
    }
}

