package com.tragoff.tragoff.service;

import com.tragoff.tragoff.model.TipoCigarro;
import com.tragoff.tragoff.repository.RegistroCigarroRepository;
import com.tragoff.tragoff.repository.TipoCigarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoCigarroService {

    @Autowired
    private TipoCigarroRepository tipoRepository;

    @Autowired
    private RegistroCigarroRepository registroRepository;

    public List<TipoCigarro> listar() {
        return tipoRepository.findAll();
    }

    public TipoCigarro incluir(TipoCigarro t) throws Exception {
        validar(t);
        if (tipoRepository.findByNome(t.getNome()) != null) {
            throw new Exception("Já existe um tipo com esse nome");
        }
        t.setId(null);
        return tipoRepository.save(t);
    }

    public TipoCigarro alterar(Long id, TipoCigarro dados) throws Exception {
        TipoCigarro existente = tipoRepository.findById(id).orElse(null);
        if (existente == null) {
            throw new Exception("Tipo de cigarro não encontrado");
        }
        validar(dados);
        TipoCigarro outro = tipoRepository.findByNome(dados.getNome());
        if (outro != null && !outro.getId().equals(id)) {
            throw new Exception("Já existe um tipo com esse nome");
        }
        existente.setNome(dados.getNome());
        existente.setUnidade(dados.getUnidade());
        existente.setPrecoUnitario(dados.getPrecoUnitario());
        return tipoRepository.save(existente);
    }

    public void excluir(Long id) throws Exception {
        if (registroRepository.existsByTipoCigarroId(id)) {
            throw new Exception("Este tipo já tem registros e não pode ser excluído");
        }
        tipoRepository.deleteById(id);
    }

    private void validar(TipoCigarro t) throws Exception {
        if (t.getNome() == null || t.getNome().trim().isEmpty()) {
            throw new Exception("Nome vazio");
        }
        String u = t.getUnidade();
        if (!"cigarros".equals(u) && !"tragadas".equals(u) && !"unidades".equals(u)) {
            throw new Exception("Escolha a unidade do produto");
        }
        // Preço padrão 0 = o usuário é obrigado a registrar a compra antes de consumir
        if (t.getPrecoUnitario() < 0) {
            throw new Exception("O preço não pode ser negativo");
        }
    }
}