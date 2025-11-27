package com.localibrary.service;

import com.localibrary.dto.BibliotecaAdminDTO;
import com.localibrary.dto.BibliotecaMapaDTO;
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
import com.localibrary.exception.DuplicateResourceException;
import com.localibrary.exception.ResourceNotFoundException;
import com.localibrary.repository.AdminRepository;
import com.localibrary.repository.BibliotecaLivroRepository;
import com.localibrary.repository.BibliotecaRepository;
import com.localibrary.repository.LivroRepository;
import com.localibrary.util.PaginationHelper;

import static com.localibrary.util.Constants.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final BibliotecaRepository bibliotecaRepository;
    private final LivroRepository livroRepository;
    private final BibliotecaLivroRepository bibliotecaLivroRepository;

    public AdminService(AdminRepository adminRepository,
                        org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
                        BibliotecaRepository bibliotecaRepository,
                        LivroRepository livroRepository,
                        BibliotecaLivroRepository bibliotecaLivroRepository) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.bibliotecaRepository = bibliotecaRepository;
        this.livroRepository = livroRepository;
        this.bibliotecaLivroRepository = bibliotecaLivroRepository;
    }

    /**
     * ✅ CORREÇÃO RF-16: Dashboard agora inclui mapa de localização
     */
    public DashboardDTO getDashboardData() {
        long totalLibs = bibliotecaRepository.count();
        long activeLibs = bibliotecaRepository.countByStatus(StatusBiblioteca.ATIVO);
        long pendingLibs = bibliotecaRepository.countByStatus(StatusBiblioteca.PENDENTE);
        long totalBooks = livroRepository.count();
        Long totalCopies = bibliotecaLivroRepository.sumTotalExemplares();

        // ✅ NOVO: Busca bibliotecas para o mapa
        List<BibliotecaMapaDTO> bibliotecasMapa = bibliotecaRepository.findAll().stream()
                .map(BibliotecaMapaDTO::new)
                .toList();

        return DashboardDTO.builder()
                .totalBibliotecas(totalLibs)
                .bibliotecasAtivas(activeLibs)
                .bibliotecasPendentes(pendingLibs)
                .totalLivrosCadastrados(totalBooks)
                .totalExemplares(totalCopies != null ? totalCopies : 0)
                .bibliotecasMapa(bibliotecasMapa)
                .build();
    }

    /**
     * RF-17, RF-19: Listar bibliotecas filtrando por status (opcional) COM PAGINAÇÃO
     * ✅ CORREÇÃO: Usa PaginationHelper
     */
    public Page<BibliotecaAdminDTO> listBibliotecas(StatusBiblioteca status, Integer page, Integer size, String sortField, String sortDir) {
        Pageable pageable = PaginationHelper.createPageable(page, size, sortField, sortDir);

        Page<Biblioteca> libsPage;
        if (status != null) {
            libsPage = bibliotecaRepository.findByStatus(status, pageable);
        } else {
            libsPage = bibliotecaRepository.findAll(pageable);
        }

        return libsPage.map(BibliotecaAdminDTO::new);
    }

    /**
     * RF-18, RF-20: Alterar status da biblioteca (Aprovar/Bloquear)
     */
    public BibliotecaAdminDTO updateBibliotecaStatus(Long id, UpdateStatusBibliotecaDTO dto) {
        Biblioteca lib = bibliotecaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_NAO_ENCONTRADO));

        lib.setStatus(dto.getStatus());
        Biblioteca saved = bibliotecaRepository.save(lib);

        return new BibliotecaAdminDTO(saved);
    }

    /**
     * RF-21: Excluir biblioteca
     */
    public void deleteBiblioteca(Long id) {
        if (!bibliotecaRepository.existsById(id)) {
            throw new ResourceNotFoundException(MSG_NAO_ENCONTRADO);
        }
        bibliotecaRepository.deleteById(id);
    }

    /**
     * RF-23: Cadastrar novos moderadores
     */
    public AdminResponseDTO createModerator(CreateModeratorRequestDTO dto) {
        if (adminRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateResourceException(MSG_DUPLICADO_EMAIL);
        }

        Admin newModerator = new Admin();
        newModerator.setNome(dto.getNome());
        newModerator.setSobrenome(dto.getSobrenome());
        newModerator.setEmail(dto.getEmail());
        newModerator.setSenha(passwordEncoder.encode(dto.getSenha()));
        newModerator.setRoleAdmin(RoleAdmin.MODERADOR);
        newModerator.setStatus(StatusAdmin.ATIVO);

        Admin savedModerator = adminRepository.save(newModerator);
        return new AdminResponseDTO(savedModerator);
    }

    /**
     * RF-22: Listar todos os moderadores
     */
    public List<AdminResponseDTO> listModerators() {
        return adminRepository.findAll().stream()
                .filter(admin -> admin.getRoleAdmin() == RoleAdmin.MODERADOR)
                .map(AdminResponseDTO::new)
                .toList();
    }

    /**
     * RF-24: Alterar status de moderador
     * ✅ CORRIGIDO: Agora funciona após adição da coluna 'status' no banco
     */
    public AdminResponseDTO updateModeratorStatus(Long id, UpdateStatusRequestDTO dto) {
        Admin moderator = findModeratorById(id);
        moderator.setStatus(dto.getStatus());
        Admin updatedModerator = adminRepository.save(moderator);
        return new AdminResponseDTO(updatedModerator);
    }

    /**
     * RF-25: Remover moderador
     */
    public void deleteModerator(Long id) {
        Admin moderator = findModeratorById(id);
        adminRepository.delete(moderator);
    }

    private Admin findModeratorById(Long id) {
        return adminRepository.findById(id)
                .filter(admin -> admin.getRoleAdmin() == RoleAdmin.MODERADOR)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_MODERADOR_NAO_ENCONTRADO + " com id: " + id));
    }
}