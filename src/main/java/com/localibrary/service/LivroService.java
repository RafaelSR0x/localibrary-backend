package com.localibrary.service;

import com.localibrary.dto.*;
import com.localibrary.dto.request.AddLivroRequestDTO;
import com.localibrary.dto.response.LivroResponseDTO;
import com.localibrary.entity.*;
import com.localibrary.enums.StatusBiblioteca;
import com.localibrary.exception.DuplicateResourceException;
import com.localibrary.exception.ResourceNotFoundException;
import com.localibrary.repository.*;
import com.localibrary.util.Constants;
import com.localibrary.util.DistanceCalculator;
import com.localibrary.util.PaginationHelper;
import com.localibrary.util.SecurityUtil;
import com.localibrary.util.ValidationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final BibliotecaRepository bibliotecaRepository;
    private final BibliotecaLivroRepository bibliotecaLivroRepository;
    private final GeneroRepository generoRepository;
    private final LivroGeneroRepository livroGeneroRepository;
    private final SecurityUtil securityUtil;

    public LivroService(LivroRepository livroRepository,
                        BibliotecaRepository bibliotecaRepository,
                        BibliotecaLivroRepository bibliotecaLivroRepository,
                        GeneroRepository generoRepository,
                        LivroGeneroRepository livroGeneroRepository,
                        SecurityUtil securityUtil) {
        this.livroRepository = livroRepository;
        this.bibliotecaRepository = bibliotecaRepository;
        this.bibliotecaLivroRepository = bibliotecaLivroRepository;
        this.generoRepository = generoRepository;
        this.livroGeneroRepository = livroGeneroRepository;
        this.securityUtil = securityUtil;
    }

    // ================================================================================
    // 🟢 MÉTODOS PÚBLICOS (BUSCA E VISUALIZAÇÃO)
    // ================================================================================

    /**
     * RF-02: Buscar livros por título (parcial, case-insensitive) COM PAGINAÇÃO
     * ✅ CORREÇÃO: Usa PaginationHelper
     */
    public Page<LivroResponseDTO> buscarLivrosPorTitulo(String titulo, Integer page, Integer size, String sortField, String sortDir) {
        Pageable pageable = PaginationHelper.createPageable(page, size, sortField, sortDir);

        return livroRepository.searchByTitulo(titulo, pageable)
                .map(LivroResponseDTO::new);
    }

    /**
     * RF-03: Buscar Livros Populares
     * ✅ CORREÇÃO: Agora usa COUNT(DISTINCT biblioteca) ao invés de SUM(quantidade)
     */
    public List<LivroResponseDTO> buscarLivrosPopulares() {
        Pageable limit = PageRequest.of(0, Constants.LIMITE_LIVROS_POPULARES);

        return livroRepository.findLivrosPopulares(limit).stream()
                .map(LivroResponseDTO::new)
                .toList();
    }

    /**
     * RF-05: Detalhes de um livro + Similares
     * ✅ CORREÇÃO: Usa findByIdWithGeneros para evitar N+1 query
     */
    public LivroDetalhesDTO buscarDetalhesDoLivro(Long id) {
        // Busca o livro COM os gêneros em uma única query
        Livro livro = livroRepository.findByIdWithGeneros(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_NAO_ENCONTRADO));

        LivroDetalhesDTO detalhesDTO = new LivroDetalhesDTO(livro);

        // Busca livros similares
        Pageable limit = PageRequest.of(0, Constants.LIMITE_LIVROS_SIMILARES);
        List<Livro> similares = livroRepository.findLivrosSimilares(id, limit).getContent();

        detalhesDTO.setLivrosSimilares(similares);

        return detalhesDTO;
    }

    /**
     * RF-06 e RN-10: Listar bibliotecas que possuem o livro.
     * APLICANDO DISTANCE CALCULATOR: Se o usuário enviar lat/lon, ordenamos por proximidade.
     */
    public List<BibliotecaParaLivroDTO> buscarBibliotecasPorLivro(Long idLivro, Double userLat, Double userLon) {
        List<Biblioteca> bibliotecas = bibliotecaRepository.findByLivroAndStatusAtivo(idLivro);

        List<BibliotecaParaLivroDTO> dtos = bibliotecas.stream()
                .filter(b -> b.getStatus() == StatusBiblioteca.ATIVO)
                .map(BibliotecaParaLivroDTO::new)
                .toList();

        // Ordenação por proximidade
        if (ValidationUtil.isValidCoordinates(userLat, userLon)) {
            dtos.sort(Comparator.comparingDouble(dto ->
                    DistanceCalculator.calculateDistance(
                            userLat, userLon,
                            dto.getLatitude().doubleValue(),
                            dto.getLongitude().doubleValue()
                    )
            ));
        }

        return dtos;
    }

    // ================================================================================
    // 🔒 MÉTODOS PROTEGIDOS (GESTÃO DE ACERVO)
    // ================================================================================

    /**
     * RF-11: Adicionar livro ao acervo da biblioteca logada.
     */
    @Transactional
    public LivroAcervoDTO addLivroToMyAcervo(Long idBiblioteca, AddLivroRequestDTO dto) {
        securityUtil.checkHasPermission(idBiblioteca);

        Biblioteca biblioteca = bibliotecaRepository.findById(idBiblioteca)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_NAO_ENCONTRADO));

        if (!ValidationUtil.isValidISBN(dto.getIsbn())) {
            throw new IllegalArgumentException(Constants.MSG_ISBN_INVALIDO);
        }

        if (dto.getAnoPublicacao() != null && !ValidationUtil.isValidAnoPublicacao(dto.getAnoPublicacao())) {
            throw new IllegalArgumentException(Constants.MSG_ANO_PUBLICACAO_INVALIDO);
        }

        Livro livro = livroRepository.findByIsbn(dto.getIsbn())
                .orElseGet(() -> createNewlivro(dto));

        if (livro.getId() == null || livro.getGeneros().isEmpty()) {
            setGenerosForLivro(livro, dto.getGenerosIds());
        }

        Livro savedlivro = livroRepository.save(livro);

        if (bibliotecaLivroRepository.existsByBibliotecaIdAndLivroId(idBiblioteca, savedlivro.getId())) {
            throw new DuplicateResourceException(Constants.MSG_DUPLICADO_ISBN);
        }

        BibliotecaLivro newRelacao = new BibliotecaLivro();
        newRelacao.setBiblioteca(biblioteca);
        newRelacao.setLivro(savedlivro);
        newRelacao.setQuantidade(dto.getQuantidade());

        if (dto.getQuantidade() == null || dto.getQuantidade() < Constants.QUANTIDADE_MINIMA_LIVRO) {
            throw new IllegalArgumentException(String.format(Constants.MSG_QUANTIDADE_MINIMA, Constants.QUANTIDADE_MINIMA_LIVRO));
        }

        BibliotecaLivro savedRelacao = bibliotecaLivroRepository.save(newRelacao);

        return new LivroAcervoDTO(savedRelacao);
    }

    /**
     * RF-12: Remover livro do acervo
     */
    @Transactional
    public void removeLivroFromMyAcervo(Long idBiblioteca, Long idLivro) {
        securityUtil.checkHasPermission(idBiblioteca);

        if (!bibliotecaLivroRepository.existsByBibliotecaIdAndLivroId(idBiblioteca, idLivro)) {
            throw new ResourceNotFoundException(Constants.MSG_NAO_ENCONTRADO);
        }

        bibliotecaLivroRepository.deleteByBibliotecaIdAndLivroId(idBiblioteca, idLivro);
    }

    /**
     * Atualizar quantidade
     */
    @Transactional
    public LivroAcervoDTO updateQuantidadeLivro(Long idBiblioteca, Long idLivro, UpdateQuantidadeDTO dto) {
        securityUtil.checkHasPermission(idBiblioteca);

        BibliotecaLivro relacao = bibliotecaLivroRepository.findByBibliotecaIdAndLivroId(idBiblioteca, idLivro)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_NAO_ENCONTRADO));

        if (dto.getQuantidade() == 0) {
            bibliotecaLivroRepository.delete(relacao);
            return null;
        }

        relacao.setQuantidade(dto.getQuantidade());
        BibliotecaLivro savedRelacao = bibliotecaLivroRepository.save(relacao);
        return new LivroAcervoDTO(savedRelacao);
    }

    // ================================================================================
    // 🛠️ MÉTODOS AUXILIARES PRIVADOS
    // ================================================================================

    private Livro createNewlivro(AddLivroRequestDTO dto) {
        Livro livro = new Livro();
        livro.setIsbn(dto.getIsbn());
        livro.setTitulo(dto.getTitulo());
        livro.setAutor(dto.getAutor());
        livro.setEditora(dto.getEditora());
        livro.setAnoPublicacao(dto.getAnoPublicacao());
        livro.setCapa(dto.getCapa());
        livro.setResumo(dto.getResumo());
        return livro;
    }

    private void setGenerosForLivro(Livro livro, Set<Long> generosIds) {
        if (livro.getGeneros() == null) {
            livro.setGeneros(new java.util.ArrayList<>());
        }

        if (livro.getId() != null) {
            livroGeneroRepository.deleteByLivroId(livro.getId());
            livro.getGeneros().clear();
        }

        for (Long generoId : generosIds) {
            Genero genero = generoRepository.findById(generoId)
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_GENERO_NAO_ENCONTRADO + " ID: " + generoId));

            LivroGenero lg = new LivroGenero();
            lg.setLivro(livro);
            lg.setGenero(genero);

            livro.getGeneros().add(lg);
        }
    }
}
