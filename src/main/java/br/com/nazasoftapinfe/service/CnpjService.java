package br.com.nazasoftapinfe.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CnpjService {

    private static final String RECEITA_WS_URL = "https://receitaws.com.br/v1/cnpj/{cnpj}";

    public String buscarDadosPorCnpj(String cnpj) {
        RestTemplate restTemplate = new RestTemplate();

        // Monta a URL com o CNPJ
        String url = UriComponentsBuilder.fromUriString(RECEITA_WS_URL)
                .buildAndExpand(cnpj)
                .toUriString();

        try {
            // Realiza a requisição para a API da ReceitaWS
            String response = restTemplate.getForObject(url, String.class);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar dados do CNPJ: " + e.getMessage());
        }
    }
}