package com.localibrary.dto;

import com.localibrary.enums.StatusBiblioteca;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusBibliotecaDTO {
    @NotNull
    private StatusBiblioteca status;
}