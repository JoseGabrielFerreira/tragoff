package com.tragoff.tragoff.controller;

import com.tragoff.tragoff.Retorno;
import com.tragoff.tragoff.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard/{usuarioId}")
    public Retorno montar(@PathVariable("usuarioId") Long usuarioId) {
        try {
            return new Retorno(dashboardService.montar(usuarioId));
        } catch (Exception ex) {
            return new Retorno(ex.getMessage());
        }
    }
}