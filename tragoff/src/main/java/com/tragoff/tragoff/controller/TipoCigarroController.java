package com.tragoff.tragoff.controller;

import com.tragoff.tragoff.Retorno;
import com.tragoff.tragoff.model.TipoCigarro;
import com.tragoff.tragoff.service.TipoCigarroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TipoCigarroController {

    @Autowired
    private TipoCigarroService tipoService;

    @GetMapping("/tipocigarro")
    public Retorno listar() {
        return new Retorno(tipoService.listar());
    }

    @PostMapping("/tipocigarro")
    public Retorno incluir(@RequestBody TipoCigarro t) {
        try {
            return new Retorno(tipoService.incluir(t));
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }

    @PutMapping("/tipocigarro/{id}")
    public Retorno alterar(@PathVariable("id") Long id, @RequestBody TipoCigarro t) {
        try {
            return new Retorno(tipoService.alterar(id, t));
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }

    @DeleteMapping("/tipocigarro/{id}")
    public Retorno excluir(@PathVariable("id") Long id) {
        try {
            tipoService.excluir(id);
            return new Retorno(id);
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }
}