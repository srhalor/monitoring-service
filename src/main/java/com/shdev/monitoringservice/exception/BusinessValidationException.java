package com.shdev.monitoringservice.exception;

/**
 * Exception thrown when a business validation fails.
 * Results in HTTP 400 Bad Request response.
 *
 * @author Shailesh Halor
 */
public class BusinessValidationException extends RuntimeException {

    public BusinessValidationException(String message) {
        super(message);
    }

    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

