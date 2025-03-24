package br.com.nazasoftapinfe.entitiy;

import br.com.nazasoftapinfe.util.LocalDateDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Getter
@Setter
public class PessoaFisica {
    @Id
    private String codigoPessoa;
    private String nomeCompleto;
    private String genero;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate dataDeNascimento;
    private String documento;
    private String nomeDaMae;
    private Integer anos;
    private String zodiaco;

    private String salarioEstimado; // Alterado de String para Double

    private String statusCadastral;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dataStatusCadastral;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate lastUpdate;

    @OneToMany(mappedBy = "pessoaFisica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Telefone> listaTelefones;

    @OneToMany(mappedBy = "pessoaFisica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Endereco> listaEnderecos;

    @OneToMany(mappedBy = "pessoaFisica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Email> listaEmails;


}
