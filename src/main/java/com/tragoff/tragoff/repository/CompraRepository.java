package com.tragoff.tragoff.repository;

import com.tragoff.tragoff.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByUsuarioIdOrderByDataCompraDesc(Long usuarioId);

    // Compras de um produto, da mais antiga para a mais nova
    List<Compra> findByUsuarioIdAndTipoCigarroIdOrderByDataCompraAscIdAsc(Long usuarioId, Long tipoCigarroId);
}
