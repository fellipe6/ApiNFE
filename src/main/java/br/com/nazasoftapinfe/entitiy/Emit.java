package br.com.nazasoftapinfe.entitiy;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@SequenceGenerator(name="EmitSeq",sequenceName = "SEQ_EMIT",allocationSize = 1)
public class Emit {

    @Id
    @GeneratedValue(generator = "EmitSeq", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String cnpj;
    private String xNome;
    private String xFant;
    private String xLgr;
    private String nro;
    private String xBairro;
    private String cMun;
    private String xMun;
    private String uf;
    private String cep;
    private String cPais;
    private String xPais;
    private String fone;
    private String ie;
    private String iest;
    private String crt;



}
