package br.com.nazasoftapinfe.entitiy;

import br.com.swconsultoria.nfe.dom.enuns.AmbienteEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="empresa")
@SequenceGenerator(name="EmpresaSeq",sequenceName = "SEQ_EMPRESA",allocationSize = 1)
@Data
public class Empresa {

    @Id
    @GeneratedValue(generator = "EmpresaSeq", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String cpfCnpj;
    private String razaoSocial;
    @Enumerated(EnumType.STRING)
    private AmbienteEnum ambiente;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[] certificado;
    private String senhaCertificado;
    private String nsu;
    private String uf;

}
