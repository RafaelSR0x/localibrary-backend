package com.localibrary.exception;

/**
 * ✅ NOVO: Exceção para erros de integração externa (API de geolocalização)
 * Será tratada como 503 SERVICE UNAVAILABLE
 */
public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
