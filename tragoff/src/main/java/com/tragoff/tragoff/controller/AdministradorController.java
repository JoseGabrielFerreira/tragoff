package com.tragoff.tragoff.controller;

import com.tragoff.tragoff.Retorno;
import com.tragoff.tragoff.model.Administrador;
import com.tragoff.tragoff.service.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    @PostMapping("/administrador/login")
    public Retorno login(@RequestBody Administrador a) {
        try {
            return new Retorno(administradorService.login(a));
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }
}