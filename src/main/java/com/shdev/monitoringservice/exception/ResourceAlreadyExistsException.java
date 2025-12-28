package com.shdev.monitoringservice.exception;

/**
 * Exception thrown when a resource already exists (e.g., duplicate key).
 * Results in HTTP 409 Conflict response.
 *
 * @author Shailesh Halor
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

