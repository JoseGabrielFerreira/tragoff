package com.tragoff.tragoff.service;

import com.tragoff.tragoff.model.Gatilho;
import com.tragoff.tragoff.repository.GatilhoRepository;
import com.tragoff.tragoff.repository.RegistroCigarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GatilhoService {

    @Autowired
    private GatilhoRepository gatilhoRepository;

    @Autowired
    private RegistroCigarroRepository registroRepository;

    public List<Gatilho> listar() {
        return gatilhoRepository.findAll();
    }

    public Gatilho incluir(Gatilho g) throws Exception {
        validar(g);
        if (gatilhoRepository.findByNome(g.getNome()) != null) {
            throw new Exception("Já existe um gatilho com esse nome");
        }
        g.setId(null);
        return gatilhoRepository.save(g);
    }

    public Gatilho alterar(Long id, Gatilho dados) throws Exception {
        Gatilho existente = gatilhoRepository.findById(id).orElse(null);
        if (existente == null) {
            throw new Exception("Gatilho não encontrado");
        }
        validar(dados);
        Gatilho outro = gatilhoRepository.findByNome(dados.getNome());
        if (outro != null && !outro.getId().equals(id)) {
            throw new Exception("Já existe um gatilho com esse nome");
        }
        existente.setNome(dados.getNome());
        return gatilhoRepository.save(existente);
    }

    public void excluir(Long id) throws Exception {
        if (registroRepository.existsByGatilhoId(id)) {
            throw new Exception("Este gatilho já tem registros e não pode ser excluído");
        }
        gatilhoRepository.deleteById(id);
    }

    private void validar(Gatilho g) throws Exception {
        if (g.getNome() == null || g.getNome().trim().isEmpty()) {
            throw new Exception("Nome vazio");
        }
    }
}