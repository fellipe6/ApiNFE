package br.com.nazasoftapinfe.entitiy;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;


@Data
@Entity
@SequenceGenerator(name = "EnderecoSeq", sequenceName = "endereco_seq", allocationSize = 1)
public class Endereco {
    @Id
    @GeneratedValue(generator = "EnderecoSeq", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;

    @ManyToOne
    @JoinColumn(name = "codigoPessoa")
    private PessoaFisica pessoaFisica;
}