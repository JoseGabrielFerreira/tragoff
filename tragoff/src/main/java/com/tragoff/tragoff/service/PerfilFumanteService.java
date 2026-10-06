package com.tragoff.tragoff.service;

import com.tragoff.tragoff.model.PerfilFumante;
import com.tragoff.tragoff.model.Usuario;
import com.tragoff.tragoff.repository.PerfilFumanteRepository;
import com.tragoff.tragoff.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PerfilFumanteService {

    @Autowired
    private PerfilFumanteRepository perfilRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Devolve null se o usuário ainda não preencheu o perfil
    public PerfilFumante buscar(Long usuarioId) {
        return perfilRepository.findByUsuarioId(usuarioId);
    }

    // Cria o perfil se não existir; se já existir, atualiza
    public PerfilFumante salvar(Long usuarioId, PerfilFumante dados) throws Exception {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado");
        }
        if (dados.getAnosFumando() < 0) {
            throw new Exception("Anos fumando inválido");
        }
        if (dados.getCigarrosPorDiaInicial() < 0) {
            throw new Exception("Quantidade de cigarros por dia inválida");
        }
        PerfilFumante perfil = perfilRepository.findByUsuarioId(usuarioId);
        if (perfil == null) {
            perfil = new PerfilFumante();
            perfil.setUsuario(usuario);
        }
        perfil.setAnosFumando(dados.getAnosFumando());
        perfil.setCigarrosPorDiaInicial(dados.getCigarrosPorDiaInicial());
        perfil.setMotivoParaParar(dados.getMotivoParaParar());
        return perfilRepository.save(perfil);
    }
}