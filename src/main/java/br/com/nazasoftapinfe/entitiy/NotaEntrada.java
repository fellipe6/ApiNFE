package br.com.nazasoftapinfe.entitiy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name="nota_entrada")
@SequenceGenerator(name="NotaEntradaSeq",sequenceName = "SEQ_NOTA_ENTRADA",allocationSize = 1)
@Data
public class NotaEntrada {
    @Id
    @GeneratedValue(generator = "NotaEntradaSeq", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String doc_schema;
    private String chave;
    private String cnpjEmitente;
    private String nomeEmitente;
    private BigDecimal valor;
    /*
    @Lob
    @Column(name = "xml", nullable = false, columnDefinition = "VARBINARY(MAX)")//Adicionado pra ser utilizado com sqlserver
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[]xml;
*/
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[]xml;
    private String serie;

    private String numeroNota;

    private String dtEmit;

    @ManyToOne
    @JoinColumn(name="empresa_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Empresa empresa;

    @Column(columnDefinition = "TEXT")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String xmlStr;
}
