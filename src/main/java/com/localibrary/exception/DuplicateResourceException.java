package com.localibrary.exception;

/**
 * ✅ NOVO: Exceção para recursos duplicados
 * Será tratada como 409 CONFLICT
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}