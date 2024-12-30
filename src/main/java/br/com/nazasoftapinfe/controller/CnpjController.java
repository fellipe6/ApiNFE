package br.com.nazasoftapinfe.controller;

import br.com.nazasoftapinfe.service.CnpjService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class CnpjController {

    private final CnpjService cnpjService;

    public CnpjController(CnpjService cnpjService) {
        this.cnpjService = cnpjService;
    }

    @GetMapping(value = "/cnpj", produces = MediaType.APPLICATION_JSON_VALUE)
    public String buscarCnpj(@RequestParam String cnpj) {
        return cnpjService.buscarDadosPorCnpj(cnpj);
    }
}