package com.localibrary.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.localibrary.entity.HorarioFuncionamento;
import com.localibrary.util.StringToBooleanDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferência de dados de horário de funcionamento
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioFuncionamentoDTO {
    private String diaSemana;
    private String horarioAbertura;
    private String horarioFechamento;
    
    @JsonDeserialize(using = StringToBooleanDeserializer.class)
    private boolean fechado;

    public HorarioFuncionamentoDTO(HorarioFuncionamento horario) {
        this.diaSemana = horario.getDiaSemana().name();
        this.horarioAbertura = horario.getHorarioAbertura() != null 
            ? horario.getHorarioAbertura().toString() 
            : null;
        this.horarioFechamento = horario.getHorarioFechamento() != null 
            ? horario.getHorarioFechamento().toString() 
            : null;
        this.fechado = horario.isFechado();
    }
}
