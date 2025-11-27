package com.localibrary.exception;

/**
 * Exceção para falhas no armazenamento de arquivos (I/O local)
 */
public class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

