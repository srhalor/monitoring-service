package com.shdev.monitoringservice.exception;

/**
 * Exception thrown when a search request contains invalid parameters.
 * Results in HTTP 400 Bad Request response.
 *
 * @author Shailesh Halor
 */
public class InvalidSearchRequestException extends RuntimeException {

    public InvalidSearchRequestException(String message) {
        super(message);
    }

    public InvalidSearchRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

