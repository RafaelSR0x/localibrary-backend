package com.localibrary.service;

import com.localibrary.dto.*;
import com.localibrary.dto.request.AddLivroRequestDTO;
import com.localibrary.dto.response.BibliotecaResponseDTO;
import com.localibrary.entity.*;
import com.localibrary.enums.StatusBiblioteca;
import com.localibrary.exception.DuplicateResourceException;
import com.localibrary.exception.ResourceNotFoundException;
import com.localibrary.repository.*;
import com.localibrary.util.Constants;
import com.localibrary.util.PaginationHelper;
import com.localibrary.util.SecurityUtil;
import com.localibrary.util.ValidationUtil;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.localibrary.util.Constants.*;

@Service
public class BibliotecaService {

    private final BibliotecaRepository bibliotecaRepository;
    private final SecurityUtil securityUtil;
    private final BibliotecaLivroRepository bibliotecaLivroRepository;
    private final LivroRepository livroRepository;
    private final GeneroRepository generoRepository;
    private final LivroGeneroRepository livroGeneroRepository;
    private final GeolocationService geolocationService;

    public BibliotecaService(BibliotecaRepository bibliotecaRepository,
                             SecurityUtil securityUtil,
                             BibliotecaLivroRepository bibliotecaLivroRepository,
                             LivroRepository livroRepository,
                             GeneroRepository generoRepository,
                             LivroGeneroRepository livroGeneroRepository,
                             GeolocationService geolocationService) {
        this.bibliotecaRepository = bibliotecaRepository;
        this.securityUtil = securityUtil;
        this.bibliotecaLivroRepository = bibliotecaLivroRepository;
        this.livroRepository = livroRepository;
        this.generoRepository = generoRepository;
        this.livroGeneroRepository = livroGeneroRepository;
        this.geolocationService = geolocationService;
    }

    /**
     * ✅ CORREÇÃO RF-04: Listar bibliotecas ATIVAS com PAGINAÇÃO
     */
    public Page<BibliotecaResponseDTO> listarBibliotecasAtivas(Integer page, Integer size, String sortField, String sortDir) {
        Pageable pageable = PaginationHelper.createPageable(page, size, sortField, sortDir);

        return bibliotecaRepository.findByStatus(StatusBiblioteca.ATIVO, pageable)
                .map(BibliotecaResponseDTO::new);
    }

    /**
     * RF-07: Detalhes de uma biblioteca
     */
    public BibliotecaDetalhesDTO buscarDetalhesBiblioteca(Long id) {
        Biblioteca biblioteca = bibliotecaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Biblioteca não encontrada com id: " + id));

        if (biblioteca.getStatus() != StatusBiblioteca.ATIVO) {
            throw new ResourceNotFoundException("Biblioteca não disponível");
        }

        return new BibliotecaDetalhesDTO(biblioteca);
    }

    /**
     * RF-13: Exibir dados detalhados da biblioteca logada (para edição)
     */
    public BibliotecaDetalhesDTO getMyBibliotecaDetails(Long idBiblioteca) {
        securityUtil.checkHasPermission(idBiblioteca);
        Biblioteca biblioteca = findBibliotecaById(idBiblioteca);
        return new BibliotecaDetalhesDTO(biblioteca);
    }

    /**
     * RF-14: Permitir a atualização dos dados de uma biblioteca
     */
    @Transactional
    public BibliotecaDetalhesDTO updateMyBiblioteca(Long idBiblioteca, UpdateBibliotecaDTO dto) {
        securityUtil.checkHasPermission(idBiblioteca);
        Biblioteca biblioteca = findBibliotecaById(idBiblioteca);

        // Validações
        if (!ValidationUtil.isValidCEP(dto.getCep())) {
            throw new IllegalArgumentException(MSG_CEP_INVALIDO);
        }
        if (ValidationUtil.isNotEmpty(dto.getTelefone()) && !ValidationUtil.isValidTelefone(dto.getTelefone())) {
            throw new IllegalArgumentException(MSG_TELEFONE_INVALIDO);
        }

        // Atualiza dados
        biblioteca.setNomeFantasia(dto.getNomeFantasia());
        biblioteca.setRazaoSocial(dto.getRazaoSocial());
        biblioteca.setTelefone(dto.getTelefone());
        biblioteca.setCategoria(dto.getCategoria());
        biblioteca.setSite(dto.getSite());
        biblioteca.setFotoBiblioteca(dto.getFotoBiblioteca());

        // Re-valida Endereço
        Endereco endereco = biblioteca.getEndereco();
        endereco.setCep(dto.getCep());
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());

        // Chama Geolocation API
        Coordinates coords = geolocationService.getCoordinatesFromAddress(
                        dto.getCep(), dto.getLogradouro(), dto.getNumero(), dto.getCidade())
                .orElseThrow(() -> new IllegalArgumentException(MSG_ENDERECO_INVALIDO));

        if (!ValidationUtil.isValidCoordinates(coords.latitude().doubleValue(), coords.longitude().doubleValue())) {
            throw new IllegalArgumentException(MSG_COORDENADAS_INVALIDAS);
        }

        endereco.setLatitude(coords.latitude());
        endereco.setLongitude(coords.longitude());

        Biblioteca bibliotecaAtualizada = bibliotecaRepository.save(biblioteca);
        return new BibliotecaDetalhesDTO(bibliotecaAtualizada);
    }

    /**
     * RF-10: Listar todos os livros disponíveis em uma biblioteca específica
     * ✅ CORREÇÃO: Usa PaginationHelper
     */
    public Page<LivroAcervoDTO> listMyLivros(Long idBiblioteca, Integer page, Integer size, String sortField, String sortDir) {
        securityUtil.checkHasPermission(idBiblioteca);

        Pageable pageable = PaginationHelper.createPageable(page, size, sortField, sortDir);

        return bibliotecaLivroRepository.findByBibliotecaId(idBiblioteca, pageable)
                .map(LivroAcervoDTO::new);
    }

    /**
     * RF-11: Permitir que bibliotecas adicionem livros ao seu acervo
     */
    @Transactional
    public LivroAcervoDTO addLivroToMyAcervo(Long idBiblioteca, AddLivroRequestDTO dto) {
        securityUtil.checkHasPermission(idBiblioteca);
        Biblioteca biblioteca = findBibliotecaById(idBiblioteca);

        if (dto.getAnoPublicacao() != null && !ValidationUtil.isValidAnoPublicacao(dto.getAnoPublicacao())) {
            throw new IllegalArgumentException(MSG_ANO_PUBLICACAO_INVALIDO);
        }

        Livro livro = livroRepository.findByIsbn(dto.getIsbn())
                .orElseGet(() -> createNewlivro(dto));

        if (livro.getId() == null || livro.getGeneros().isEmpty()) {
            setGenerosForLivro(livro, dto.getGenerosIds());
        }

        Livro savedlivro = livroRepository.save(livro);

        if (bibliotecaLivroRepository.existsByBibliotecaIdAndLivroId(idBiblioteca, savedlivro.getId())) {
            throw new DuplicateResourceException(MSG_DUPLICADO_ISBN);
        }

        BibliotecaLivro newRelacao = new BibliotecaLivro();
        newRelacao.setBiblioteca(biblioteca);
        newRelacao.setLivro(savedlivro);
        newRelacao.setQuantidade(dto.getQuantidade());

        BibliotecaLivro savedRelacao = bibliotecaLivroRepository.save(newRelacao);

        return new LivroAcervoDTO(savedRelacao);
    }

    /**
     * RF-12: Permitir que bibliotecas removam livros de seu acervo
     */
    @Transactional
    public void removeLivroFromMyAcervo(Long idBiblioteca, Long idLivro) {
        securityUtil.checkHasPermission(idBiblioteca);

        if (!bibliotecaLivroRepository.existsByBibliotecaIdAndLivroId(idBiblioteca, idLivro)) {
            throw new ResourceNotFoundException(MSG_LIVRO_NAO_ENCONTRADO_ACERVO);
        }

        bibliotecaLivroRepository.deleteByBibliotecaIdAndLivroId(idBiblioteca, idLivro);
    }

    /**
     * Atualiza a quantidade de um livro no acervo
     */
    @Transactional
    public LivroAcervoDTO updateQuantidadeLivro(Long idBiblioteca, Long idLivro, UpdateQuantidadeDTO dto) {
        securityUtil.checkHasPermission(idBiblioteca);

        BibliotecaLivro relacao = bibliotecaLivroRepository.findByBibliotecaIdAndLivroId(idBiblioteca, idLivro)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_LIVRO_NAO_ENCONTRADO));

        if (dto.getQuantidade() == null || dto.getQuantidade() < Constants.QUANTIDADE_MINIMA_LIVRO) {
            throw new IllegalArgumentException(String.format(Constants.MSG_QUANTIDADE_MINIMA, Constants.QUANTIDADE_MINIMA_LIVRO));
        }

        if (dto.getQuantidade() == 0) {
            bibliotecaLivroRepository.delete(relacao);
            return null;
        }

        relacao.setQuantidade(dto.getQuantidade());
        BibliotecaLivro savedRelacao = bibliotecaLivroRepository.save(relacao);
        return new LivroAcervoDTO(savedRelacao);
    }

    /**
     * RF-12: Retorna todas as informações do livro para edição (apenas para bibliotecas que têm o livro no acervo)
     */
    public LivroDetalhesDTO getLivroForEdit(Long idBiblioteca, Long idLivro) {
        securityUtil.checkHasPermission(idBiblioteca);

        // Verifica se o livro pertence ao acervo da biblioteca
        if (!bibliotecaLivroRepository.existsByBibliotecaIdAndLivroId(idBiblioteca, idLivro)) {
            throw new ResourceNotFoundException(MSG_LIVRO_NAO_ENCONTRADO_ACERVO);
        }

        Livro livro = livroRepository.findByIdWithGeneros(idLivro)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_LIVRO_NAO_ENCONTRADO));

        return new LivroDetalhesDTO(livro);
    }

    /**
     * RF-13: Atualiza informações do livro (campos editáveis) e salva no banco
     */
    @Transactional
    public LivroDetalhesDTO updateLivroInLibrary(Long idBiblioteca, Long idLivro, com.localibrary.dto.request.UpdateLivroRequestDTO dto) {
        securityUtil.checkHasPermission(idBiblioteca);

        // Verifica se o livro pertence ao acervo da biblioteca
        if (!bibliotecaLivroRepository.existsByBibliotecaIdAndLivroId(idBiblioteca, idLivro)) {
            throw new ResourceNotFoundException(MSG_LIVRO_NAO_ENCONTRADO_ACERVO);
        }

        Livro livro = livroRepository.findByIdWithGeneros(idLivro)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_LIVRO_NAO_ENCONTRADO));

        // Validações mínimas
        if (dto.getAnoPublicacao() != null && !ValidationUtil.isValidAnoPublicacao(dto.getAnoPublicacao())) {
            throw new IllegalArgumentException(MSG_ANO_PUBLICACAO_INVALIDO);
        }

        // Atualiza campos editáveis
        livro.setTitulo(dto.getTitulo());
        livro.setAutor(dto.getAutor());
        livro.setEditora(dto.getEditora());
        livro.setAnoPublicacao(dto.getAnoPublicacao());
        livro.setCapa(dto.getCapa());
        livro.setResumo(dto.getResumo());

        // Atualiza gêneros
        setGenerosForLivro(livro, dto.getGenerosIds());

        Livro saved = livroRepository.save(livro);

        return new LivroDetalhesDTO(saved);
    }

    // --- Métodos Auxiliares ---

    private Biblioteca findBibliotecaById(Long id) {
        return bibliotecaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Biblioteca não encontrada com id: " + id));
    }

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

        livroGeneroRepository.deleteByLivroId(livro.getId());
        livro.getGeneros().clear();

        for (Long generoId : generosIds) {
            Genero genero = generoRepository.findById(generoId)
                    .orElseThrow(() -> new ResourceNotFoundException(MSG_GENERO_NAO_ENCONTRADO + " ID: " + generoId));

            LivroGenero lgRelacao = new LivroGenero();
            lgRelacao.setLivro(livro);
            lgRelacao.setGenero(genero);

            livro.getGeneros().add(lgRelacao);
        }
    }
}
