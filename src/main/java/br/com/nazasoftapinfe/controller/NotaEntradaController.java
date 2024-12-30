package br.com.nazasoftapinfe.controller;

import br.com.nazasoftapinfe.service.DistribuicaoService;
import br.com.nazasoftapinfe.service.NotaEntradaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    @GetMapping(value ="consulta",produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(value = "xml/{chave}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getXml(@PathVariable("chave") String chave) {
        try {
            // Obtém o XML como string
            String xml = notaEntradaService.getXml(chave);
            log.info("XML recebido: {}", xml);

            // Converte o XML para JSON
            XmlMapper xmlMapper = new XmlMapper();
            Object jsonObject = xmlMapper.readValue(xml, Object.class);
            log.info("Objeto JSON gerado: {}", jsonObject);

            // Retorna a resposta JSON
            return ResponseEntity.ok(jsonObject);
        } catch (IOException e) {
            log.error("Erro ao processar o XML: ", e);
            return ResponseEntity.status(500).body("Erro ao processar o XML: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado: ", e);
            return ResponseEntity.status(500).body("Erro inesperado: " + e.getMessage());
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
