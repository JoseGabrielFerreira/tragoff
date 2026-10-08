package com.tragoff.tragoff.controller;

import com.tragoff.tragoff.Retorno;
import com.tragoff.tragoff.model.PerfilFumante;
import com.tragoff.tragoff.service.PerfilFumanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PerfilFumanteController {

    @Autowired
    private PerfilFumanteService perfilService;

    @GetMapping("/perfil/{usuarioId}")
    public Retorno buscar(@PathVariable("usuarioId") Long usuarioId) {
        return new Retorno(perfilService.buscar(usuarioId));
    }

    @PostMapping("/perfil/{usuarioId}")
    public Retorno salvar(@PathVariable("usuarioId") Long usuarioId, @RequestBody PerfilFumante p) {
        try {
            return new Retorno(perfilService.salvar(usuarioId, p));
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }
}