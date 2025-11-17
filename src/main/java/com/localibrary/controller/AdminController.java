package com.localibrary.controller;

import com.localibrary.dto.response.AdminResponseDTO;
import com.localibrary.dto.request.CreateModeratorRequestDTO;
import com.localibrary.dto.request.UpdateStatusRequestDTO;
import com.localibrary.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/moderadores")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // RF-23: Permitir cadastro de novos moderadores pelo administrador.
    @PostMapping
    public ResponseEntity<AdminResponseDTO> createModerator(
            @Valid @RequestBody CreateModeratorRequestDTO dto
    ) {
        AdminResponseDTO newModerator = adminService.createModerator(dto);
        return new ResponseEntity<>(newModerator, HttpStatus.CREATED);
    }

    // RF-22: Listar todos os moderadores cadastrados no sistema.
    @GetMapping
    public ResponseEntity<List<AdminResponseDTO>> listModerators() {
        List<AdminResponseDTO> moderators = adminService.listModerators();
        return ResponseEntity.ok(moderators);
    }

    // RF-24: Alterar status de moderador (ativar/desativar).
    @PatchMapping("/{id}")
    public ResponseEntity<AdminResponseDTO> updateModeratorStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequestDTO dto
    ) {
        AdminResponseDTO updatedModerator = adminService.updateModeratorStatus(id, dto);
        return ResponseEntity.ok(updatedModerator);
    }

    // RF-25: Permitir remoção de moderadores do sistema.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModerator(@PathVariable Long id) {
        adminService.deleteModerator(id);
        return ResponseEntity.noContent().build();
    }
}
