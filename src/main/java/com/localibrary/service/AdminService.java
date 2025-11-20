package com.localibrary.service;

import com.localibrary.dto.BibliotecaAdminDTO;
import com.localibrary.dto.DashboardDTO;
import com.localibrary.dto.UpdateStatusBibliotecaDTO;
import com.localibrary.dto.response.AdminResponseDTO;
import com.localibrary.dto.request.CreateModeratorRequestDTO;
import com.localibrary.dto.request.UpdateStatusRequestDTO;
import com.localibrary.entity.Admin;
import com.localibrary.entity.Biblioteca;
import com.localibrary.enums.RoleAdmin;
import com.localibrary.enums.StatusAdmin;
import com.localibrary.enums.StatusBiblioteca;
import com.localibrary.repository.AdminRepository;
import com.localibrary.repository.BibliotecaLivroRepository;
import com.localibrary.repository.BibliotecaRepository;
import com.localibrary.repository.LivroRepository;
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

    @Autowired
    private BibliotecaRepository bibliotecaRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private BibliotecaLivroRepository bibliotecaLivroRepository;

    /**
     * RF-16: Dashboard Administrativo
     */
    public DashboardDTO getDashboardData() {
        long totalLibs = bibliotecaRepository.count();
        long activeLibs = bibliotecaRepository.countByStatus(StatusBiblioteca.ATIVO);
        long pendingLibs = bibliotecaRepository.countByStatus(StatusBiblioteca.PENDENTE);
        long totalBooks = livroRepository.count();
        Long totalCopies = bibliotecaLivroRepository.sumTotalExemplares();

        return DashboardDTO.builder()
                .totalBibliotecas(totalLibs)
                .bibliotecasAtivas(activeLibs)
                .bibliotecasPendentes(pendingLibs)
                .totalLivrosCadastrados(totalBooks)
                .totalExemplares(totalCopies != null ? totalCopies : 0)
                .build();
    }

    /**
     * RF-17, RF-19: Listar bibliotecas filtrando por status (opcional)
     */
    public List<BibliotecaAdminDTO> listBibliotecas(StatusBiblioteca status) {
        List<Biblioteca> libs;
        if (status != null) {
            libs = bibliotecaRepository.findByStatus(status);
        } else {
            libs = bibliotecaRepository.findAll();
        }

        return libs.stream()
                .map(BibliotecaAdminDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * RF-18, RF-20: Alterar status da biblioteca (Aprovar/Bloquear)
     */
    public BibliotecaAdminDTO updateBibliotecaStatus(Long id, UpdateStatusBibliotecaDTO dto) {
        Biblioteca lib = bibliotecaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Biblioteca não encontrada."));

        lib.setStatus(dto.getStatus());
        Biblioteca saved = bibliotecaRepository.save(lib);

        return new BibliotecaAdminDTO(saved);
    }

    /**
     * RF-21: Excluir biblioteca
     */
    public void deleteBiblioteca(Long id) {
        if (!bibliotecaRepository.existsById(id)) {
            throw new EntityNotFoundException("Biblioteca não encontrada.");
        }
        // O CascadeType.ALL na entidade cuidará do Endereço, Credenciais e Livros
        bibliotecaRepository.deleteById(id);
    }

    // RF-23: Cadastrar novos moderadores
    public AdminResponseDTO createModerator(CreateModeratorRequestDTO dto) {
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
