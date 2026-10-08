package com.tragoff.tragoff.controller;

import com.tragoff.tragoff.Retorno;
import com.tragoff.tragoff.model.Gatilho;
import com.tragoff.tragoff.service.GatilhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GatilhoController {

    @Autowired
    private GatilhoService gatilhoService;

    @GetMapping("/gatilho")
    public Retorno listar() {
        return new Retorno(gatilhoService.listar());
    }

    @PostMapping("/gatilho")
    public Retorno incluir(@RequestBody Gatilho g) {
        try {
            return new Retorno(gatilhoService.incluir(g));
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }

    @PutMapping("/gatilho/{id}")
    public Retorno alterar(@PathVariable("id") Long id, @RequestBody Gatilho g) {
        try {
            return new Retorno(gatilhoService.alterar(id, g));
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }

    @DeleteMapping("/gatilho/{id}")
    public Retorno excluir(@PathVariable("id") Long id) {
        try {
            gatilhoService.excluir(id);
            return new Retorno(id);
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }
}