package br.com.nazasoftapinfe.entitiy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Generated;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="nota_entrada")
@SequenceGenerator(name="NotaEntradaSeq",sequenceName = "SEQ_NOTA_ENTRADA",allocationSize = 1)
@Data
public class NotaEntrada {
    @Id
    @GeneratedValue(generator = "NotaEntradaSeq", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String schema;
    private String chave;
    private String cnpjEmitente;

    private String nomeEmitente;
    private BigDecimal valor;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[]xml;

    @ManyToOne
    @JoinColumn(name="empresa_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Empresa empresa;

}
