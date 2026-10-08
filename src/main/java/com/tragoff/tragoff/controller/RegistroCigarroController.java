package com.tragoff.tragoff.controller;

import com.tragoff.tragoff.Retorno;
import com.tragoff.tragoff.model.RegistroCigarro;
import com.tragoff.tragoff.service.RegistroCigarroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegistroCigarroController {

    @Autowired
    private RegistroCigarroService registroService;

    @PostMapping("/registro")
    public Retorno registrar(@RequestBody RegistroCigarro r) {
        try {
            return new Retorno(registroService.registrar(r));
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }

    @GetMapping("/registro/usuario/{usuarioId}")
    public Retorno listar(@PathVariable("usuarioId") Long usuarioId) {
        return new Retorno(registroService.listarPorUsuario(usuarioId));
    }

    @DeleteMapping("/registro/{id}")
    public Retorno excluir(@PathVariable("id") Long id) {
        try {
            registroService.excluir(id);
            return new Retorno(id);
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }
}