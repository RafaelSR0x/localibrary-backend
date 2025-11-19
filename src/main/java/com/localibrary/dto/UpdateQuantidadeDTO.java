package com.localibrary.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateQuantidadeDTO {
    @NotNull
    @Min(0) // 0 pode significar remover
    private int quantidade;
}