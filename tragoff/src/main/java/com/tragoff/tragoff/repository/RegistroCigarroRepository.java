package com.tragoff.tragoff.repository;

import com.tragoff.tragoff.model.RegistroCigarro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface RegistroCigarroRepository extends JpaRepository<RegistroCigarro, Long> {

    // Todos os registros de um usuário, do mais recente para o mais antigo
    @Query("select r from RegistroCigarro r where r.usuario.id = :usuarioId order by r.dataHora desc")
    List<RegistroCigarro> buscarPorUsuario(@Param("usuarioId") Long usuarioId);

    // Registros de um usuário dentro de um período (usado para hoje, semana e mês)
    List<RegistroCigarro> findByUsuarioIdAndDataHoraBetween(Long usuarioId, Date inicio, Date fim);

    // Usados na área admin para não apagar tipo/gatilho que já tem registros
    boolean existsByTipoCigarroId(Long tipoCigarroId);
    boolean existsByGatilhoId(Long gatilhoId);
}