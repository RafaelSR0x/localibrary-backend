package com.localibrary.exception;

import com.localibrary.dto.ApiErrorDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

import static com.localibrary.util.Constants.*;

/**
 * GlobalExceptionHandler padronizado para usar exceções customizadas do domínio.
 * Mudança: removido o mapeamento para EntityNotFoundException/EntityExistsException
 * em favor de ResourceNotFoundException e DuplicateResourceException padronizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ============================================================
    // 404 - NOT FOUND
    // ============================================================

    /**
     * Trata recursos não encontrados (padronizado com ResourceNotFoundException)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleResourceNotFoundPrimary(ResourceNotFoundException ex) {
        logger.warn("Recurso não encontrado: {}", ex.getMessage());
        String msg = ex.getMessage() != null ? ex.getMessage() : MSG_NAO_ENCONTRADO;
        return buildResponse(HttpStatus.NOT_FOUND, msg, null);
    }

    // ============================================================
    // 409 - CONFLICT
    // ============================================================

    /**
     * Trata conflitos de duplicação (email já existe, CNPJ duplicado, etc.)
     * Preferência por DuplicateResourceException (lançada explicitamente pelos services)
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorDTO> handleDuplicateResourcePrimary(DuplicateResourceException ex) {
        logger.warn("Recurso duplicado: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    /**
     * ✅ NOVO: Trata violações de integridade do banco (UNIQUE, FK, etc.)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("Violação de integridade do banco de dados", ex);

        String message = MSG_CONFLITO;
        List<String> errors = new ArrayList<>();

        // Extrai mensagem amigável baseada na causa raiz
        String rootMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

        if (rootMessage != null) {
            if (rootMessage.contains("Duplicate entry")) {
                message = "Este dado já está cadastrado no sistema.";

                if (rootMessage.contains("cnpj")) {
                    errors.add("CNPJ já cadastrado");
                } else if (rootMessage.contains("email")) {
                    errors.add("Email já cadastrado");
                } else if (rootMessage.contains("isbn")) {
                    errors.add("ISBN já cadastrado");
                }
            } else if (rootMessage.contains("foreign key constraint")) {
                message = "Não é possível realizar esta operação devido a vínculos existentes.";
            } else if (rootMessage.contains("cannot be null")) {
                message = "Campo obrigatório não foi preenchido.";
            }
        }

        return buildResponse(HttpStatus.CONFLICT, message, errors.isEmpty() ? null : errors);
    }

    // ============================================================
    // 400 - BAD REQUEST
    // ============================================================

    /**
     * Trata erros de validação do Bean Validation (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.warn("Erro de validação de entrada");

        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList(); // Sonar: use Stream.toList()

        return buildResponse(HttpStatus.BAD_REQUEST, MSG_DADOS_INVALIDOS, errors);
    }

    /**
     * ✅ NOVO: Trata violações de constraints do JPA (@NotNull, @Size, etc.)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDTO> handleConstraintViolation(ConstraintViolationException ex) {
        logger.warn("Violação de constraint: {}", ex.getMessage());

        List<String> errors = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .toList(); // Sonar: use Stream.toList()

        return buildResponse(HttpStatus.BAD_REQUEST, MSG_DADOS_INVALIDOS, errors);
    }

    /**
     * ✅ NOVO: Trata JSON malformado ou inválido
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.warn("JSON malformado ou inválido");

        String message = "JSON inválido ou malformado.";
        List<String> errors = new ArrayList<>();

        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Required request body is missing")) {
                message = "Corpo da requisição é obrigatório.";
            } else if (ex.getMessage().contains("JSON parse error")) {
                message = "Erro ao processar JSON. Verifique a sintaxe.";
                errors.add("Dica: Verifique vírgulas, chaves e aspas");
            }
        }

        return buildResponse(HttpStatus.BAD_REQUEST, message, errors.isEmpty() ? null : errors);
    }

    /**
     * ✅ NOVO: Trata parâmetros com tipo inválido (ex: enviar "abc" para um Long)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorDTO> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.warn("Tipo de parâmetro inválido: {} esperava {}", ex.getName(), ex.getRequiredType());

        String message = String.format("Parâmetro '%s' deve ser do tipo %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "válido");

        return buildResponse(HttpStatus.BAD_REQUEST, message, null);
    }

    /**
     * ✅ NOVO: Trata parâmetros obrigatórios ausentes
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorDTO> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        logger.warn("Parâmetro obrigatório ausente: {}", ex.getParameterName());

        String message = String.format("Parâmetro obrigatório '%s' está ausente", ex.getParameterName());

        return buildResponse(HttpStatus.BAD_REQUEST, message, null);
    }

    /**
     * Trata argumentos inválidos
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Argumento inválido: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    /**
     * ✅ NOVO: Trata estados inválidos da aplicação
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalState(IllegalStateException ex) {
        logger.error("Estado inválido da aplicação: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    // ============================================================
    // 401 - UNAUTHORIZED
    // ============================================================

    /**
     * Trata erros de autenticação (senha incorreta, token inválido)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorDTO> handleAuthenticationException(AuthenticationException ex) {
        logger.warn("Falha de autenticação: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, MSG_NAO_AUTORIZADO, null);
    }

    // ============================================================
    // 403 - FORBIDDEN
    // ============================================================

    /**
     * Trata erros de autorização (RN-01: tentar acessar recurso de outra biblioteca)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDTO> handleAccessDenied(AccessDeniedException ex) {
        logger.warn("Acesso negado: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, MSG_PROIBIDO, null);
    }

    // ============================================================
    // 500 - INTERNAL SERVER ERROR
    // ============================================================

    // ============================================================
    // 503 - SERVICE UNAVAILABLE
    // ============================================================

    /**
     * ✅ NOVO: Trata falhas em serviços externos (API de geolocalização)
     */
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiErrorDTO> handleExternalServiceException(ExternalServiceException ex) {
        logger.error("Falha em serviço externo: {}", ex.getMessage());
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), null);
    }

    /**
     * Trata falhas de armazenamento de arquivos (uploads)
     */
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ApiErrorDTO> handleStorageException(StorageException ex) {
        logger.error("Erro de armazenamento: {}", ex.getMessage());
        String message = "Erro ao processar arquivo. Tente novamente mais tarde.";
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }


    /**
     * Catch-all para exceções não previstas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGenericException(Exception ex) {
        logger.error("Erro interno do servidor", ex);

        // Em produção, não expor stacktrace
        List<String> errors = new ArrayList<>();
        errors.add("ID do erro: " + System.currentTimeMillis()); // Para rastreamento

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, MSG_ERRO_GENERICO, errors);
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    /**
     * Constrói resposta padronizada de erro
     */
    private ResponseEntity<ApiErrorDTO> buildResponse(HttpStatus status, String msg, List<String> errors) {
        ApiErrorDTO error = new ApiErrorDTO(status.value(), msg, errors);
        return new ResponseEntity<>(error, status);
    }
}