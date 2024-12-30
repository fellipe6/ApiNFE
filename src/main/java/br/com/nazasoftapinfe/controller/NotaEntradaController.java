package br.com.nazasoftapinfe.controller;

import br.com.nazasoftapinfe.service.DistribuicaoService;
import br.com.nazasoftapinfe.service.LogService;
import br.com.nazasoftapinfe.service.NotaEntradaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/notaEntrada/")

public class NotaEntradaController {

    private final NotaEntradaService notaEntradaService;
    private final DistribuicaoService distribuicaoService;
    private final LogService logService;
    private static final Logger log = LoggerFactory.getLogger(NotaEntradaController.class);

    public NotaEntradaController(NotaEntradaService notaEntradaService, DistribuicaoService distribuicaoService, LogService logService) {
        this.notaEntradaService = notaEntradaService;
        this.distribuicaoService = distribuicaoService;
        this.logService = logService;
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
    @GetMapping(value ="/listarNotas",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listarNotas() {
        try {
            return ResponseEntity.ok(notaEntradaService.listarNotas());
        } catch (Exception e) {
            log.error("Erro ao listar notas ", e);
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
    public ResponseEntity<?> getXml(@PathVariable("chave") String chave, HttpServletRequest request) {
        String clientIp = getClientIp(request); // Obtém o IP do cliente

        try {
            // Obtém o XML como string
            String xml = notaEntradaService.getXml(chave);
            log.info("IP: {} | XML recebido: {}", clientIp, xml);

            // Converte o XML para JSON
            XmlMapper xmlMapper = new XmlMapper();
            Object jsonObject = xmlMapper.readValue(xml, Object.class);
            log.info("IP: {} | Objeto JSON gerado: {}", clientIp, jsonObject);

            // Salva o log no banco
            logService.salvarLog("INFO", "Consulta de XML realizada com sucesso. Chave: " + chave, null, clientIp);

            // Retorna a resposta JSON
            return ResponseEntity.ok(jsonObject);
        } catch (IOException e) {
            log.error("IP: {} | Erro ao processar o XML: ", clientIp, e);
            logService.salvarLog("ERROR", "Erro ao processar XML", e.getMessage(), clientIp);
            return ResponseEntity.status(500).body("Erro ao processar o XML: " + e.getMessage());
        } catch (Exception e) {
            log.error("IP: {} | Erro inesperado: ", clientIp, e);
            logService.salvarLog("ERROR", "Erro inesperado ao processar XML", e.getMessage(), clientIp);
            return ResponseEntity.status(500).body("Erro inesperado: " + e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For"); // Obtém IP real se houver proxy

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP"); // Alguns proxies usam esse cabeçalho
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP"); // WebLogic usa esse cabeçalho
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP"); // Alternativa usada por alguns servidores
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR"); // Outra alternativa para proxies
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr(); // Captura diretamente do request
        }

        // Se o IP for "::1" (IPv6 de localhost), converte para "127.0.0.1"
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return (ip != null) ? ip : "IP não identificado";
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
