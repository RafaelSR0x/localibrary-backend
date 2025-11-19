package com.localibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorDTO {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private List<String> errors; // Para múltiplos erros (ex: validação de campos)

    public ApiErrorDTO(int status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }
}