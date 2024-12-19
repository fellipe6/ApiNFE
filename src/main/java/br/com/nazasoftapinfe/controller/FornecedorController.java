package br.com.nazasoftapinfe.controller;

import br.com.nazasoftapinfe.entitiy.Empresa;
import br.com.nazasoftapinfe.service.EmpresaService;
import br.com.nazasoftapinfe.service.FornecedorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/fornecedor")
@Slf4j
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }


    @GetMapping
    public ResponseEntity<?> listarFornecedor() {
        try {
            return ResponseEntity.ok(fornecedorService.listaFornecedor());
        } catch (Exception e) {
            log.error("Erro ao listar fornecedores ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}