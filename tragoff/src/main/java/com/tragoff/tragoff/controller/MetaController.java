package com.tragoff.tragoff.controller;

import com.tragoff.tragoff.Retorno;
import com.tragoff.tragoff.model.Meta;
import com.tragoff.tragoff.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MetaController {

    @Autowired
    private MetaService metaService;

    @PostMapping("/meta")
    public Retorno criar(@RequestBody Meta m) {
        try {
            return new Retorno(metaService.criar(m));
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }

    @GetMapping("/meta/usuario/{usuarioId}")
    public Retorno listar(@PathVariable("usuarioId") Long usuarioId) {
        return new Retorno(metaService.listarPorUsuario(usuarioId));
    }

    @PutMapping("/meta/{id}/status/{novoStatus}")
    public Retorno mudarStatus(@PathVariable("id") Long id, @PathVariable("novoStatus") String novoStatus) {
        try {
            return new Retorno(metaService.mudarStatus(id, novoStatus));
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }
}