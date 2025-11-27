package com.localibrary.security;

import com.localibrary.entity.Admin;
import com.localibrary.entity.CredencialBiblioteca;
import com.localibrary.repository.AdminRepository;
import com.localibrary.repository.CredencialBibliotecaRepository;
import com.localibrary.util.Constants;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final CredencialBibliotecaRepository credenciaisRepository;

    public UserDetailsServiceImpl(AdminRepository adminRepository, CredencialBibliotecaRepository credenciaisRepository) {
        this.adminRepository = adminRepository;
        this.credenciaisRepository = credenciaisRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            return new UserDetailsImpl(admin.get());
        }
        Optional<CredencialBiblioteca> credenciais = credenciaisRepository.findByEmail(email);
        if (credenciais.isPresent()) {
            return new UserDetailsImpl(credenciais.get());
        }
        throw new UsernameNotFoundException(Constants.MSG_USUARIO_NAO_ENCONTRADO + " com o email: " + email);
    }
}