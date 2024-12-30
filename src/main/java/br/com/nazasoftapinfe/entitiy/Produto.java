package br.com.nazasoftapinfe.entitiy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="produto")
@SequenceGenerator(name="ProdutoSeq",sequenceName = "SEQ_PRODUTO",allocationSize = 1)
@Data
public class Produto {

    @Id
    @GeneratedValue(generator = "ProdutoSeq", strategy = GenerationType.SEQUENCE)
    private Long id;

    private String chave;

    @Column(name = "cProd")
    private String cProd;

    @Column(name = "cEAN")
    private String cEAN;

    @Column(name = "xProd")
    private String xProd;

    @Column(name = "vProd")
    private BigDecimal vProd;

    @Column(name = "NCM")
    private String ncm;

    @Column(name = "CEST")
    private String cest;

    @Column(name = "CFOP")
    private String cfop;

    @Column(name = "uCom")
    private String ucom;

    @Column(name = "qCom")
    private String qcom;

    @Column(name = "vUnCom")
    private String vUnCom;

    @Column(name = "cEANTrib")
    private String cEANTrib;

    @Column(name = "uTrib")
    private String uTrib;

    @Column(name = "qTrib")
    private String qTrib;

    @Column(name = "vUnTrib")
    private String vUnTrib;

    @Column(name = "nLote")
    private String nLote;

    @Column(name = "qLote")
    private String qLote;


    @Column(name = "dFab")
    private String dFab;


    @Column(name = "dVal")
    private String dVal;

    @ManyToOne
    @JoinColumn(name="empresa_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Empresa empresa;

    private String CNPJDestino;

}
