package com.healthcare.dao;

/**
 * Lightweight runtime exception for DAO layer failures.
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

