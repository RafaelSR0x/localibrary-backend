package com.localibrary.exception;

/**
 * ✅ NOVO: Exceção para recursos não encontrados (alternativa ao EntityNotFoundException)
 * Será tratada como 404 NOT FOUND
 */
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s não encontrado com %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Construtor de conveniência que aceita apenas uma mensagem personalizada.
     */
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }

    /**
     * Construtor padrão sem detalhes (poderá usar a mensagem padrão).
     */
    public ResourceNotFoundException() {
        super("Recurso não encontrado");
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}