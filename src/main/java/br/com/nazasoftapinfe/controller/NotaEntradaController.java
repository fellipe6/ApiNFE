package br.com.nazasoftapinfe.controller;

import br.com.nazasoftapinfe.service.DistribuicaoService;
import br.com.nazasoftapinfe.service.NotaEntradaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notaEntrada/")
@Slf4j
public class NotaEntradaController {

    private final NotaEntradaService notaEntradaService;
    private final DistribuicaoService distribuicaoService;

    public NotaEntradaController(NotaEntradaService notaEntradaService, DistribuicaoService distribuicaoService) {
        this.notaEntradaService = notaEntradaService;
        this.distribuicaoService = distribuicaoService;
    }

    @GetMapping(value ="consulta")
    public ResponseEntity<?> consulta() {
        try {
            distribuicaoService.consultaNotas();
            return ResponseEntity.ok(listarNotas());
        } catch (Exception e) {
            log.error("Erro ao listar empresas ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listarNotas() {
        try {
            return ResponseEntity.ok(notaEntradaService.listarNotas());
        } catch (Exception e) {
            log.error("Erro ao listar empresas ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
        @GetMapping("{id}")
        public ResponseEntity<?> listarPorID(@PathVariable("id") Long idNotaEntrada) {
            try {
                return ResponseEntity.ok(notaEntradaService.listarPorID(idNotaEntrada));
            } catch (Exception e) {
                log.error("Erro ao listar empresas", e);
                return  ResponseEntity.badRequest().body(e.getMessage());
            }

    }

    @GetMapping(value="xml/{id}")
    public ResponseEntity<?> getXml(@PathVariable("id") Long idNotaEntrada) {
        try {
            return ResponseEntity.ok(notaEntradaService.getXml(idNotaEntrada));
        } catch (Exception e) {
            log.error("Erro ao listar empresas", e);
            return  ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @GetMapping(value="chave/{chave}")
    public ResponseEntity<?> getPorChave(@PathVariable("chave") String chave) {
        try {
            return ResponseEntity.ok(notaEntradaService.getPorChave(chave));
        } catch (Exception e) {
            log.error("Erro ao listar empresas", e);
            return  ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
