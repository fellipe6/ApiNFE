package br.com.nazasoftapinfe.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CepService {

    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    public String buscarEnderecoPorCep(String cep) {
        RestTemplate restTemplate = new RestTemplate();

        // Montar URL
        String url = UriComponentsBuilder.fromUriString(VIA_CEP_URL)
                .buildAndExpand(cep)
                .toUriString();

        // Fazer a requisição para o ViaCEP
        try {
            String response = restTemplate.getForObject(url, String.class);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar CEP: " + e.getMessage());
        }
    }
}