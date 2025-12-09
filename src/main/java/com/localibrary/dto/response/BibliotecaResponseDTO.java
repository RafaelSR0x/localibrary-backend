package com.localibrary.dto.response;

import com.localibrary.dto.HorarioFuncionamentoDTO;
import com.localibrary.entity.Biblioteca;
import com.localibrary.entity.Endereco;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class BibliotecaResponseDTO {
    private Long id;
    private String nomeFantasia;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String logradouro;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private List<HorarioFuncionamentoDTO> horariosFuncionamento;

    public BibliotecaResponseDTO(Biblioteca biblioteca) {
        this.id = biblioteca.getId();
        this.nomeFantasia = biblioteca.getNomeFantasia();

        Endereco e = biblioteca.getEndereco();
        if (e != null) {
            this.latitude = e.getLatitude();
            this.longitude = e.getLongitude();
            this.logradouro = e.getLogradouro();
            this.numero = e.getNumero();
            this.bairro = e.getBairro();
            this.cidade = e.getCidade();
            this.estado = e.getEstado();
        }

        // Mapear horários de funcionamento
        this.horariosFuncionamento = biblioteca.getHorariosFuncionamento() != null
            ? biblioteca.getHorariosFuncionamento()
                .stream()
                .map(HorarioFuncionamentoDTO::new)
                .collect(Collectors.toList())
            : new ArrayList<>();
    }
}