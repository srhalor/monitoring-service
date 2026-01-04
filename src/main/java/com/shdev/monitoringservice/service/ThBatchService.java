package com.shdev.monitoringservice.service;

import com.shdev.monitoringservice.exception.ResourceNotFoundException;
import com.shdev.omsdatabase.dto.ErrorDetailDto;
import com.shdev.omsdatabase.entity.ErrorDetailEntity;
import com.shdev.omsdatabase.mapper.ErrorDetailMapper;
import com.shdev.omsdatabase.repository.ErrorDetailEntityRepository;
import com.shdev.omsdatabase.repository.ThBatchEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for Thunderhead Batch operations.
 * Provides functionality to retrieve batch-related information.
 *
 * @author Shailesh Halor
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ThBatchService {

    private final ThBatchEntityRepository batchRepository;
    private final ErrorDetailEntityRepository errorDetailRepository;
    private final ErrorDetailMapper errorDetailMapper;

    /**
     * Get all error details associated with a Thunderhead batch.
     * API 3.5: GET /batches/{batchId}/errors
     *
     * @param batchId the batch ID
     * @return list of error details (empty list if no errors)
     * @throws ResourceNotFoundException if batch doesn't exist
     */
    @Transactional(readOnly = true)
    public List<ErrorDetailDto> getErrorsByBatchId(Long batchId) {
        log.info("Fetching error details for batch ID: {}", batchId);

        // Verify batch exists
        if (!batchRepository.existsById(batchId)) {
            throw new ResourceNotFoundException("Batch not found with ID: " + batchId);
        }

        List<ErrorDetailEntity> errorEntities = errorDetailRepository.findByOmtbe_Id(batchId);

        log.info("Found {} error details for batch ID: {}", errorEntities.size(), batchId);

        return errorEntities.stream()
                .map(errorDetailMapper::toDto)
                .toList();
    }
}

