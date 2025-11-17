package com.localibrary.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

// DTO interno para guardar coordenadas
record Coordinates(BigDecimal latitude, BigDecimal longitude) {
}

@Service
public class GeolocationService {

    /**
     * Simula a chamada à API do Google Maps (RN-17).
     * Em um projeto real, aqui você usaria RestTemplate ou WebClient
     * para chamar a API do Google com sua 'app.google.api-key'.
     */
    public Optional<Coordinates> getCoordinatesFromAddress(String cep, String logradouro, String numero, String cidade) {

        // --- SIMULAÇÃO ---
        // Se o endereço for da Av. Paulista, retorna coordenadas válidas.
        if (logradouro.equalsIgnoreCase("Av. Paulista") && cidade.equalsIgnoreCase("São Paulo")) {
            return Optional.of(new Coordinates(
                    new BigDecimal("-23.5614"),
                    new BigDecimal("-46.6560")
            ));
        }

        // Simula um endereço que a API não encontrou (RN-18)
        if (cep.equals("00000-000")) {
            return Optional.empty();
        }

        // Simulação padrão para outros endereços
        return Optional.of(new Coordinates(
                new BigDecimal("-23.5505"), // Centro de SP
                new BigDecimal("-46.6333")
        ));
        // --- FIM DA SIMULAÇÃO ---
    }
}
