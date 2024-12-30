package br.com.nazasoftapinfe.controller;

import br.com.nazasoftapinfe.service.CepService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping(value="/cep", produces = MediaType.APPLICATION_JSON_VALUE)
    public String buscarCep(@RequestParam String cep) {
        return cepService.buscarEnderecoPorCep(cep);
    }
}