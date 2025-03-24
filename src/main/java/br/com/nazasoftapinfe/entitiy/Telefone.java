package br.com.nazasoftapinfe.entitiy;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@SequenceGenerator(name = "TelefoneSeq", sequenceName = "telefone_seq", allocationSize = 1)
public class Telefone {
    @Id
    @GeneratedValue(generator = "TelefoneSeq", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String telefoneComDDD;
    private String telemarketingBloqueado;
    private String operadora;
    private String tipoTelefone;
    private String whatsApp;

    @ManyToOne
    @JoinColumn(name = "codigoPessoa")
    private PessoaFisica pessoaFisica;
}