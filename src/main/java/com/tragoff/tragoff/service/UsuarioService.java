package com.tragoff.tragoff.service;

import com.tragoff.tragoff.model.Pessoa;
import com.tragoff.tragoff.model.Usuario;
import com.tragoff.tragoff.repository.PessoaRepository;
import com.tragoff.tragoff.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    public Usuario cadastrar(Usuario u) throws Exception {
        validar(u);
        validarSenha(u.getSenha());
        if (pessoaRepository.findByEmail(u.getEmail()) != null) {
            throw new Exception("Já existe uma conta com esse e-mail");
        }
        u.setId(null);
        u.setDataCadastro(new Date());
        return usuarioRepository.save(u);
    }

    public Usuario login(Usuario u) throws Exception {
        if (u.getEmail() == null || u.getEmail().trim().isEmpty()) {
            throw new Exception("E-mail vazio");
        }
        if (u.getSenha() == null || u.getSenha().trim().isEmpty()) {
            throw new Exception("Senha vazia");
        }
        Usuario encontrado = usuarioRepository.findByEmailAndSenha(u.getEmail(), u.getSenha());
        if (encontrado == null) {
            throw new Exception("E-mail ou senha inválidos");
        }
        return encontrado;
    }

    public Usuario buscar(Long id) throws Exception {
        Usuario u = usuarioRepository.findById(id).orElse(null);
        if (u == null) {
            throw new Exception("Usuário não encontrado");
        }
        return u;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario atualizar(Long id, Usuario dados) throws Exception {
        Usuario existente = buscar(id);
        validar(dados);
        Pessoa outra = pessoaRepository.findByEmail(dados.getEmail());
        if (outra != null && !outra.getId().equals(id)) {
            throw new Exception("Já existe uma conta com esse e-mail");
        }
        existente.setNome(dados.getNome());
        existente.setEmail(dados.getEmail());
        // senha vazia = mantém a senha atual
        if (dados.getSenha() != null && !dados.getSenha().trim().isEmpty()) {
            validarSenha(dados.getSenha());
            existente.setSenha(dados.getSenha());
        }
        return usuarioRepository.save(existente);
    }

    private void validar(Usuario u) throws Exception {
        if (u.getNome() == null || u.getNome().trim().isEmpty()) {
            throw new Exception("Nome vazio");
        }
        if (u.getEmail() == null || u.getEmail().trim().isEmpty()
                || !u.getEmail().contains("@") || !u.getEmail().contains(".")) {
            throw new Exception("E-mail inválido");
        }
    }

    private void validarSenha(String senha) throws Exception {
        if (senha == null || senha.trim().isEmpty()) {
            throw new Exception("Senha vazia");
        }
        if (senha.length() < 4 || senha.length() > 50) {
            throw new Exception("A senha deve ter entre 4 e 50 caracteres");
        }
    }
}