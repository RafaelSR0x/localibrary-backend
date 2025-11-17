package com.localibrary.service;

import com.localibrary.dto.response.AdminResponseDTO;
import com.localibrary.dto.request.CreateModeratorRequestDTO;
import com.localibrary.dto.request.UpdateStatusRequestDTO;
import com.localibrary.entity.Admin;
import com.localibrary.enums.RoleAdmin;
import com.localibrary.enums.StatusAdmin;
import com.localibrary.repository.AdminRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // RF-23: Cadastrar novos moderadores
    public AdminResponseDTO createModerator(CreateModeratorRequestDTO dto) {
        // Validação (RN-04 é tratada pelo SecurityConfig)
        if (adminRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EntityExistsException("Email já cadastrado.");
        }

        Admin newModerator = new Admin();
        newModerator.setNome(dto.getNome());
        newModerator.setSobrenome(dto.getSobrenome());
        newModerator.setEmail(dto.getEmail());
        newModerator.setSenha(passwordEncoder.encode(dto.getSenha()));
        newModerator.setRoleAdmin(RoleAdmin.MODERADOR);
        newModerator.setStatus(StatusAdmin.ATIVO); // Padrão

        Admin savedModerator = adminRepository.save(newModerator);
        return new AdminResponseDTO(savedModerator);
    }

    // RF-22: Listar todos os moderadores
    public List<AdminResponseDTO> listModerators() {
        return adminRepository.findAll().stream()
                .filter(admin -> admin.getRoleAdmin() == RoleAdmin.MODERADOR)
                .map(AdminResponseDTO::new) // Converte Admin para AdminResponseDTO
                .collect(Collectors.toList());
    }

    // RF-24: Alterar status de moderador
    public AdminResponseDTO updateModeratorStatus(Long id, UpdateStatusRequestDTO dto) {
        Admin moderator = findModeratorById(id);
        moderator.setStatus(dto.getStatus());
        Admin updatedModerator = adminRepository.save(moderator);
        return new AdminResponseDTO(updatedModerator);
    }

    // RF-25: Remover moderador
    public void deleteModerator(Long id) {
        Admin moderator = findModeratorById(id);
        adminRepository.delete(moderator);
    }

    // Método auxiliar para evitar repetição
    private Admin findModeratorById(Long id) {
        return adminRepository.findById(id)
                .filter(admin -> admin.getRoleAdmin() == RoleAdmin.MODERADOR)
                .orElseThrow(() -> new EntityNotFoundException("Moderador não encontrado com id: " + id));
    }
}
