package com.localibrary.dto.response;

import com.localibrary.entity.Admin;
import com.localibrary.enums.RoleAdmin;
import com.localibrary.enums.StatusAdmin;
import lombok.Data;

@Data
public class AdminResponseDTO {
    private Long id;
    private String nome;
    private String sobrenome;
    private String email;
    private RoleAdmin roleAdmin;
    private StatusAdmin status;

    public AdminResponseDTO(Admin admin) {
        this.id = admin.getId();
        this.nome = admin.getNome();
        this.sobrenome = admin.getSobrenome();
        this.email = admin.getEmail();
        this.roleAdmin = admin.getRoleAdmin();
        this.status = admin.getStatus();
    }
}
