package com.healthcare.service;

/**
 * Runtime exception to signal service-layer failures.
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

