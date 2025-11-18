package com.localibrary.service;

import com.localibrary.dto.BibliotecaDetalhesDTO;
import com.localibrary.dto.response.BibliotecaResponseDTO;
import com.localibrary.entity.Biblioteca;
import com.localibrary.enums.StatusBiblioteca;
import com.localibrary.repository.BibliotecaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BibliotecaService {

    @Autowired
    private BibliotecaRepository bibliotecaRepository;

    /**
     * RF-04: Exibir mapa com todas as bibliotecas ATIVAS em SP
     * (Usando sua query 'findBibliotecasAtivasEmSaoPaulo')
     */
    public List<BibliotecaResponseDTO> listarBibliotecasAtivas() {
        return bibliotecaRepository.findBibliotecasAtivasEmSaoPaulo().stream()
                .map(BibliotecaResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * RF-07: Detalhes de uma biblioteca
     * (Query 'findById' é padrão, mas verificamos o status)
     */
    public BibliotecaDetalhesDTO buscarDetalhesBiblioteca(Long id) {
        Biblioteca biblioteca = bibliotecaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Biblioteca não encontrada com id: " + id));

        // RN-02: Se a biblioteca não estiver ATIVA, não mostrar
        // (Sua query de RF-06 já faz isso, mas a de RF-07 não,
        // então mantemos a verificação no service).
        if (biblioteca.getStatus() != StatusBiblioteca.ATIVO) {
            throw new EntityNotFoundException("Biblioteca não disponível");
        }

        return new BibliotecaDetalhesDTO(biblioteca);
    }
}