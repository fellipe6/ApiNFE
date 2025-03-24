package br.com.nazasoftapinfe.entitiy;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@SequenceGenerator(name = "EmailSeq", sequenceName = "email_seq", allocationSize = 1)
public class Email {
    @Id
    @GeneratedValue(generator = "EmailSeq", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String enderecoEmail;

    @ManyToOne
    @JoinColumn(name = "codigoPessoa")
    private PessoaFisica pessoaFisica;
}
