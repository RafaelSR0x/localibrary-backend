package com.localibrary.service;

import com.localibrary.dto.BibliotecaRegistrationDTO;
import com.localibrary.dto.request.LoginRequestDTO;
import com.localibrary.dto.response.LoginResponseDTO;
import com.localibrary.entity.Biblioteca;
import com.localibrary.entity.CredencialBiblioteca;
import com.localibrary.entity.Endereco;
import com.localibrary.enums.StatusBiblioteca;
import com.localibrary.repository.BibliotecaRepository;
import com.localibrary.repository.CredencialBibliotecaRepository;
import com.localibrary.repository.EnderecoRepository;
import com.localibrary.security.JwtTokenService;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private BibliotecaRepository bibliotecaRepository;

    @Autowired
    private CredencialBibliotecaRepository credenciaisRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private GeolocationService geolocationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        // Cria o objeto de autenticação
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getSenha()
                )
        );

        // Seta a autenticação no contexto de segurança
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Gera o token JWT
        String token = jwtTokenService.generateToken(authentication);

        return new LoginResponseDTO(token, "Bearer", 86400L); // 24h em segundos
    }

    /**
     * RF-08: Permitir o cadastro de uma nova biblioteca.
     * Transacional para garantir que ou salva tudo, ou não salva nada.
     */
    @Transactional
    public void registerBiblioteca(BibliotecaRegistrationDTO dto) {
        // 1. Validar duplicidade
        if (credenciaisRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EntityExistsException("Este email já está em uso.");
        }
        if (bibliotecaRepository.findByCnpj(dto.getCnpj()).isPresent()) {
            throw new EntityExistsException("Este CNPJ já está em uso.");
        }

        // 2. Chamar API de Geolocalização (RN-17)
        Optional<Coordinates> coordsOpt = geolocationService.getCoordinatesFromAddress(
                dto.getCep(), dto.getLogradouro(), dto.getNumero(), dto.getCidade()
        );

        // 3. Validar retorno da API (RN-18)
        if (coordsOpt.isEmpty()) {
            throw new IllegalArgumentException("Endereço inválido ou não encontrado. Verifique os dados.");
        }
        Coordinates coords = coordsOpt.get();

        // 4. Salvar Endereço
        Endereco endereco = new Endereco();
        endereco.setCep(dto.getCep());
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setLatitude(coords.latitude());
        endereco.setLongitude(coords.longitude());
        Endereco savedEndereco = enderecoRepository.save(endereco); // Precisamos do EnderecoRepository

        // 5. Salvar Biblioteca
        Biblioteca biblioteca = new Biblioteca();
        biblioteca.setNomeFantasia(dto.getNomeFantasia());
        biblioteca.setRazaoSocial(dto.getRazaoSocial());
        biblioteca.setCnpj(dto.getCnpj());
        biblioteca.setTelefone(dto.getTelefone());
        biblioteca.setCategoria(dto.getCategoria());
        biblioteca.setSite(dto.getSite());
        biblioteca.setStatus(StatusBiblioteca.PENDENTE); // Status Padrão (RN-02)
        biblioteca.setEndereco(savedEndereco);
        Biblioteca savedBiblioteca = bibliotecaRepository.save(biblioteca); // Precisamos do BibliotecaRepository

        // 6. Salvar Credenciais
        CredencialBiblioteca credenciais = new CredencialBiblioteca();
        credenciais.setEmail(dto.getEmail());
        credenciais.setSenha(passwordEncoder.encode(dto.getSenha()));
        credenciais.setBiblioteca(savedBiblioteca);
        credenciaisRepository.save(credenciais);
    }

}