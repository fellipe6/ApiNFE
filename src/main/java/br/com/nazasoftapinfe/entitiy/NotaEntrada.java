package br.com.nazasoftapinfe.entitiy;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String cnpj;
    private String schema;
    private String chave;
    private String cnpjEmitente;

    private String nomeEmitente;
    private BigDecimal valor;
    private byte[]xml;

    @ManyToOne
    @JoinColumn(name="empresa_id")
    private Empresa empresa;

}
