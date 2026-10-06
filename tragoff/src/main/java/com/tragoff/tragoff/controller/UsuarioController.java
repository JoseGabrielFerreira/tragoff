package com.tragoff.tragoff.controller;

import com.tragoff.tragoff.Retorno;
import com.tragoff.tragoff.model.Usuario;
import com.tragoff.tragoff.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/usuario")
    public Retorno cadastrar(@RequestBody Usuario u) {
        try {
            return new Retorno(usuarioService.cadastrar(u));
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public Retorno login(@RequestBody Usuario u) {
        try {
            return new Retorno(usuarioService.login(u));
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }

    @GetMapping("/usuario")
    public Retorno listar() {
        return new Retorno(usuarioService.listar());
    }

    @GetMapping("/usuario/{id}")
    public Retorno buscar(@PathVariable("id") Long id) {
        try {
            return new Retorno(usuarioService.buscar(id));
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }

    @PutMapping("/usuario/{id}")
    public Retorno atualizar(@PathVariable("id") Long id, @RequestBody Usuario u) {
        try {
            return new Retorno(usuarioService.atualizar(id, u));
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }
}