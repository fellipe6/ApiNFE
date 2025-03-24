package br.com.nazasoftapinfe.service;

import br.com.nazasoftapinfe.entitiy.PessoaFisica;
import br.com.nazasoftapinfe.entitiy.PessoaFisicaResponse;
import br.com.nazasoftapinfe.repository.PessoaFisicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class PessoaFisicaService {
    private static final Logger logger = LoggerFactory.getLogger(PessoaFisicaService.class);

    @Autowired
    private PessoaFisicaRepository pessoaFisicaRepository;

    private final String apiUrl = "https://ws.hubdodesenvolvedor.com.br/v2/cadastropf/?cpf={cpf}&token=23741430tBFZCPYxoV42864432";

    public PessoaFisica buscarEGravarPessoaFisica(String cpf) {
        logger.info("🔍 Iniciando busca para CPF: {}", cpf);

        RestTemplate restTemplate = new RestTemplate();
        PessoaFisicaResponse response = null;

        try {
            response = restTemplate.getForObject(apiUrl, PessoaFisicaResponse.class, cpf);
            logger.info("✅ API externa respondeu: {}", response);
        } catch (Exception e) {
            logger.error("❌ Erro ao chamar API externa: {}", e.getMessage());
            return null;
        }

        if (response != null && response.isStatus()) {
            PessoaFisica pessoaFisica = response.getResult();
            logger.info("📥 Dados recebidos: {}", pessoaFisica);

            return pessoaFisicaRepository.save(pessoaFisica);
        }

        logger.warn("⚠️ API não retornou dados válidos.");
        return null;
    }


    /**
     * Converte uma string de data no formato dd/MM/yyyy para LocalDate.
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty() || dateStr.contains("Entre em contato")) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            logger.error("❌ Erro ao converter data: {}", dateStr);
            return null;
        }
    }

    /**
     * Converte um valor de String para Double, removendo caracteres inválidos.
     */
    private Double parseDouble(String value) {
        try {
            if (value == null || value.isEmpty()) {
                return null;
            }
            // Remove caracteres inválidos e substitui vírgula por ponto
            String sanitizedValue = value.replaceAll("[^0-9.,]", "").replace(",", ".");
            return Double.parseDouble(sanitizedValue);
        } catch (NumberFormatException e) {
            logger.error("❌ Erro ao converter salário estimado: {}", value);
            return null;
        }
    }
}
