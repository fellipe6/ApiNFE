package br.com.nazasoftapinfe.entitiy;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="fornecedor")
@SequenceGenerator(name="FornecedorSeq",sequenceName = "SEQ_FORNECEDOR",allocationSize = 1)
@Data
public class Fornecedor implements Serializable {

    @Id
    @GeneratedValue(generator = "FornecedorSeq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "cnpj_emitente", length = 14, nullable = false) // UNIQUE e NOT NULL
    private String cnpj;

    @Column(name = "nome_emitente", length = 100)
    private String nome;

    @Column(name="xLgr")
    private String logradouro;

    @Column(name="nro")
    private String numero;

    private String xCpl;
    private String xBairro;
    private String cMun;
    private String xMun;
    private String UF;
    private String CEP;
    private String cPais;
    private String xPais;
    private String IE;
    private String crt;

    @ManyToOne
    @JoinColumn(name="empresa_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Empresa empresa;

    private String CNPJDestino;
}
