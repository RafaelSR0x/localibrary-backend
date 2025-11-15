package com.localibrary.service;

import com.localibrary.dto.request.CadastroBibliotecaRequestDTO;
import com.localibrary.dto.request.LoginRequestDTO;
import com.localibrary.dto.response.TokenResponseDTO;
import com.localibrary.entity.Biblioteca;
import com.localibrary.entity.CredencialBiblioteca;
import com.localibrary.entity.Endereco;
import com.localibrary.enums.StatusBiblioteca;
import com.localibrary.exception.BusinessException;
import com.localibrary.exception.UnauthorizedException;
import com.localibrary.repository.BibliotecaRepository;
import com.localibrary.repository.CredencialBibliotecaRepository;
import com.localibrary.security.CustomUserDetails;
import com.localibrary.security.CustomUserDetailsService;
import com.localibrary.security.JwtTokenProvider;
import com.localibrary.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de autenticação.
 * Gerencia login e cadastro de bibliotecas e admins.
 *
 * RF-08: Cadastro de biblioteca
 * RF-09: Login de biblioteca
 * RF-15: Login de admin/moderador
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BibliotecaRepository bibliotecaRepository;

    @Autowired
    private CredencialBibliotecaRepository credencialRepository;

    @Autowired
    private EnderecoService enderecoService;

    /**
     * Realiza login de usuário (biblioteca ou admin).
     *
     * Fluxo:
     * 1. Autentica credenciais via AuthenticationManager
     * 2. Carrega UserDetails completo
     * 3. Gera token JWT
     * 4. Retorna TokenResponseDTO
     *
     * @param request DTO com email e senha
     * @return TokenResponseDTO com token e dados do usuário
     * @throws UnauthorizedException se credenciais inválidas
     */
    public TokenResponseDTO login(LoginRequestDTO request) {

        try {
            // 1. Autenticar via Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getSenha()
                    )
            );

            // 2. Carregar UserDetails completo
            CustomUserDetails userDetails = (CustomUserDetails)
                    userDetailsService.loadUserByUsername(request.getEmail());

            // 3. Verificar se usuário está ativo
            if (!userDetails.isEnabled()) {
                throw new UnauthorizedException(
                        "Usuário inativo. Entre em contato com o administrador."
                );
            }

            // 4. Gerar token JWT
            String token = tokenProvider.generateToken(userDetails);

            // 5. Buscar nome do usuário
            String nome = buscarNomeUsuario(userDetails);

            // 6. Retornar resposta
            logger.info("Login realizado com sucesso: {} ({})",
                    userDetails.getEmail(), userDetails.getTipo());

            return new TokenResponseDTO(
                    token,
                    userDetails.getId(),
                    userDetails.getEmail(),
                    userDetails.getTipo(),
                    userDetails.getRole(),
                    nome
            );

        } catch (BadCredentialsException ex) {
            logger.error("Tentativa de login com credenciais inválidas: {}", request.getEmail());
            throw new UnauthorizedException("Email ou senha inválidos");
        }
    }

    /**
     * Cadastra nova biblioteca.
     *
     * Fluxo:
     * 1. Validar dados
     * 2. Verificar duplicações (CNPJ, email)
     * 3. Criar endereço (com validação de geolocalização)
     * 4. Criar biblioteca (status PENDENTE)
     * 5. Criar credencial (senha criptografada)
     * 6. Salvar tudo (transação)
     *
     * RF-08: Cadastro de biblioteca
     * RN-12: Um endereço por biblioteca
     *
     * @param request DTO com dados do cadastro
     * @throws BusinessException se dados inválidos ou duplicados
     */
    @Transactional
    public void cadastrarBiblioteca(CadastroBibliotecaRequestDTO request) {

        // 1. Validar dados
        validarCadastro(request);

        // 2. Verificar CNPJ duplicado
        if (bibliotecaRepository.findByCnpj(request.getCnpj()).isPresent()) {
            throw new BusinessException(
                    "CNPJ já cadastrado: " + request.getCnpj(),
                    "CNPJ_DUPLICADO"
            );
        }

        // 3. Verificar email duplicado
        if (credencialRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                    "Email já cadastrado: " + request.getEmail(),
                    "EMAIL_DUPLICADO"
            );
        }

        // 4. Criar endereço (valida e busca coordenadas)
        Endereco endereco = enderecoService.criarEndereco(request.getEndereco());

        // 5. Criar biblioteca
        Biblioteca biblioteca = new Biblioteca();
        biblioteca.setNomeFantasia(request.getNomeFantasia());
        biblioteca.setRazaoSocial(request.getRazaoSocial());
        biblioteca.setCnpj(request.getCnpj());
        biblioteca.setTelefone(request.getTelefone());
        biblioteca.setCategoria(request.getCategoria());
        biblioteca.setSite(request.getSite());
        biblioteca.setStatus(StatusBiblioteca.PENDENTE); // RN-02: inicial como PENDENTE
        biblioteca.setEndereco(endereco);

        // 6. Salvar biblioteca
        biblioteca = bibliotecaRepository.save(biblioteca);

        // 7. Criar credencial
        CredencialBiblioteca credencial = new CredencialBiblioteca();
        credencial.setBiblioteca(biblioteca);
        credencial.setEmail(request.getEmail());
        credencial.setSenha(passwordEncoder.encode(request.getSenha()));

        // 8. Salvar credencial
        credencialRepository.save(credencial);

        logger.info("Biblioteca cadastrada com sucesso: {} (CNPJ: {})",
                biblioteca.getNomeFantasia(), biblioteca.getCnpj());
    }

    /**
     * Valida dados do cadastro.
     *
     * @param request DTO a validar
     * @throws BusinessException se dados inválidos
     */
    private void validarCadastro(CadastroBibliotecaRequestDTO request) {

        // Validar CNPJ
        if (!ValidationUtil.isValidCNPJ(request.getCnpj())) {
            throw new BusinessException("CNPJ inválido: " + request.getCnpj());
        }

        // Validar email
        if (!ValidationUtil.isValidEmail(request.getEmail())) {
            throw new BusinessException("Email inválido: " + request.getEmail());
        }

        // Validar telefone (se informado)
        if (request.getTelefone() != null &&
                !ValidationUtil.isValidTelefone(request.getTelefone())) {
            throw new BusinessException("Telefone inválido: " + request.getTelefone());
        }

        // Validar senha
        if (!ValidationUtil.isValidSenha(request.getSenha())) {
            throw new BusinessException(
                    "Senha deve ter entre 6 e 255 caracteres"
            );
        }
    }

    /**
     * Busca nome do usuário para resposta do login.
     *
     * @param userDetails Detalhes do usuário autenticado
     * @return Nome do usuário
     */
    private String buscarNomeUsuario(CustomUserDetails userDetails) {

        if ("BIBLIOTECA".equals(userDetails.getTipo())) {
            // Buscar nome fantasia da biblioteca
            return bibliotecaRepository.findById(userDetails.getId())
                    .map(Biblioteca::getNomeFantasia)
                    .orElse("Biblioteca");
        } else {
            // Para admin, já temos o nome em CustomUserDetails
            // mas podemos buscar nome completo se necessário
            return "Admin"; // TODO: buscar nome completo se necessário
        }
    }
}