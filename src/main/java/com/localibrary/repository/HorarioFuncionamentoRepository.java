package com.localibrary.repository;

import com.localibrary.entity.HorarioFuncionamento;
import com.localibrary.enums.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de horários de funcionamento
 */
@Repository
public interface HorarioFuncionamentoRepository extends JpaRepository<HorarioFuncionamento, Long> {
    
    /**
     * Busca todos os horários de uma biblioteca
     */
    List<HorarioFuncionamento> findByBibliotecaId(Long idBiblioteca);
    
    /**
     * Busca horário de um dia específico de uma biblioteca
     */
    Optional<HorarioFuncionamento> findByBibliotecaIdAndDiaSemana(Long idBiblioteca, DiaSemana diaSemana);
    
    /**
     * Deleta todos os horários de uma biblioteca
     */
    void deleteByBibliotecaId(Long idBiblioteca);
}
