package com.tragoff.tragoff.controller;

import com.tragoff.tragoff.Retorno;
import com.tragoff.tragoff.model.Compra;
import com.tragoff.tragoff.service.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @PostMapping("/compra")
    public Retorno registrar(@RequestBody Compra c) {
        try {
            return new Retorno(compraService.registrar(c));
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }

    @GetMapping("/compra/usuario/{usuarioId}")
    public Retorno listar(@PathVariable("usuarioId") Long usuarioId) {
        return new Retorno(compraService.listarPorUsuario(usuarioId));
    }
}
