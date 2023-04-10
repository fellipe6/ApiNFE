package br.com.nazasoftapinfe.controller;

import br.com.nazasoftapinfe.entitiy.Empresa;
import br.com.nazasoftapinfe.service.EmpresaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/empresa")
@Slf4j
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody Empresa empresa) {
        try {
            empresaService.salvar(empresa);
            return ResponseEntity.ok(empresa);
        } catch (Exception e) {
            log.error("Erro ao salvar empresa", e);
           return  ResponseEntity.badRequest().body(e.getMessage());
        }


    }
    @GetMapping
    public ResponseEntity<?> listarEmpresas() {
        try {
            return ResponseEntity.ok(empresaService.listarEmpresas());
        } catch (Exception e) {
            log.error("Erro ao listar empresas ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
        @GetMapping("{id}")
        public ResponseEntity<?> listarPorID(@PathVariable("id") Long idEmpresa) {
            try {
                return ResponseEntity.ok(empresaService.listarPorID(idEmpresa));
            } catch (Exception e) {
                log.error("Erro ao listar empresas", e);
                return  ResponseEntity.badRequest().body(e.getMessage());
            }

    }
    @DeleteMapping("{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idEmpresa) {
        try {
            empresaService.deletar(idEmpresa);
            return ResponseEntity.ok("Empresa excluída com sucesso!");
            } catch (Exception e) {
            log.error("Erro ao listar empresas", e);
            return ResponseEntity.badRequest().body("Erro ao excluir empresa" + e.getMessage());
        }

    }


}
