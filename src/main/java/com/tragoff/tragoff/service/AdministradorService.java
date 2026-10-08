package com.tragoff.tragoff.service;

import com.tragoff.tragoff.model.Administrador;
import com.tragoff.tragoff.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    public Administrador login(Administrador a) throws Exception {
        if (a.getEmail() == null || a.getEmail().trim().isEmpty()) {
            throw new Exception("E-mail vazio");
        }
        if (a.getSenha() == null || a.getSenha().trim().isEmpty()) {
            throw new Exception("Senha vazia");
        }
        Administrador encontrado = administradorRepository.findByEmailAndSenha(a.getEmail(), a.getSenha());
        if (encontrado == null) {
            throw new Exception("E-mail ou senha inválidos");
        }
        return encontrado;
    }
}