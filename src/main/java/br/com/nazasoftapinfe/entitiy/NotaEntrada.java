package br.com.nazasoftapinfe.entitiy;

import br.com.nazasoftapinfe.enums.TipoEndereco;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="nota_entrada")
@SequenceGenerator(name="NotaEntradaSeq",sequenceName = "SEQ_NOTA_ENTRADA",allocationSize = 1)
@Data
public class NotaEntrada implements Serializable {
    @Id
    @GeneratedValue(generator = "NotaEntradaSeq", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String doc_schema;
    private String chave;
    private String cnpjEmitente;
    private String nomeEmitente;
    private BigDecimal valor;

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
