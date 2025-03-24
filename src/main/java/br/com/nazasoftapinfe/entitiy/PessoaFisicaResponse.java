package br.com.nazasoftapinfe.entitiy;

import lombok.Data;

@Data
public class PessoaFisicaResponse {
    private boolean status;
    private String retorno;
    private int consumed;
    private PessoaFisica result;
}