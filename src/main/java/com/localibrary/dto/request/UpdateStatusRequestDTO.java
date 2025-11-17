package com.localibrary.dto.request;

import com.localibrary.enums.StatusAdmin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequestDTO {
    @NotNull(message = "Status não pode ser nulo")
    private StatusAdmin status;
}