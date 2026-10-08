package com.tragoff.tragoff.service;

import com.tragoff.tragoff.model.Compra;
import com.tragoff.tragoff.model.RegistroCigarro;
import com.tragoff.tragoff.model.TipoCigarro;
import com.tragoff.tragoff.model.Usuario;
import com.tragoff.tragoff.repository.CompraRepository;
import com.tragoff.tragoff.repository.RegistroCigarroRepository;
import com.tragoff.tragoff.repository.TipoCigarroRepository;
import com.tragoff.tragoff.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoCigarroRepository tipoRepository;

    @Autowired
    private RegistroCigarroRepository registroRepository;

    public Compra registrar(Compra c) throws Exception {
        if (c.getUsuario() == null || c.getUsuario().getId() == null) {
            throw new Exception("Usuário não informado");
        }
        if (c.getTipoCigarro() == null || c.getTipoCigarro().getId() == null) {
            throw new Exception("Escolha o produto");
        }
        if (c.getPrecoPago() <= 0) {
            throw new Exception("O preço pago deve ser maior que zero");
        }
        if (c.getQuantidadeTotal() <= 0) {
            throw new Exception("A quantidade total deve ser maior que zero");
        }
        if (c.getQuantidadeTotal() > 1000000) {
            throw new Exception("Quantidade total muito alta");
        }

        Usuario usuario = usuarioRepository.findById(c.getUsuario().getId()).orElse(null);
        TipoCigarro tipo = tipoRepository.findById(c.getTipoCigarro().getId()).orElse(null);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado");
        }
        if (tipo == null) {
            throw new Exception("Produto não encontrado");
        }

        c.setId(null);
        c.setUsuario(usuario);
        c.setTipoCigarro(tipo);
        c.setDataCompra(new Date());
        Compra salva = compraRepository.save(c);
        salva.setRestante(salva.getQuantidadeTotal());
        return salva;
    }

    // Lista as compras (da mais nova para a mais antiga), já com o estoque restante de cada uma
    public List<Compra> listarPorUsuario(Long usuarioId) {
        List<Compra> compras = compraRepository.findByUsuarioIdOrderByDataCompraDesc(usuarioId);
        for (Compra c : compras) {
            c.setRestante(calcularRestante(c));
        }
        return compras;
    }

    // Restante = quantidade comprada - tudo o que já foi consumido dessa compra
    public int calcularRestante(Compra c) {
        int usado = 0;
        List<RegistroCigarro> consumos = registroRepository.findByCompraId(c.getId());
        for (RegistroCigarro r : consumos) {
            usado = usado + r.getQuantidade();
        }
        int restante = c.getQuantidadeTotal() - usado;
        if (restante < 0) {
            restante = 0;
        }
        return restante;
    }

    // A compra mais antiga do produto que ainda tem unidades (ou null se acabou tudo)
    public Compra buscarCompraEmUso(Long usuarioId, Long tipoId) {
        List<Compra> compras = compraRepository
                .findByUsuarioIdAndTipoCigarroIdOrderByDataCompraAscIdAsc(usuarioId, tipoId);
        for (Compra c : compras) {
            c.setRestante(calcularRestante(c));
            if (c.getRestante() > 0) {
                return c;
            }
        }
        return null;
    }

    // O usuário já registrou alguma compra desse produto?
    public boolean jaComprou(Long usuarioId, Long tipoId) {
        return !compraRepository
                .findByUsuarioIdAndTipoCigarroIdOrderByDataCompraAscIdAsc(usuarioId, tipoId).isEmpty();
    }
}