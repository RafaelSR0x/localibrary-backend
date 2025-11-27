package com.localibrary.exception;

/**
 * ✅ NOVO: Exceção para violações de regras de negócio
 * Será tratada como 400 BAD REQUEST
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}