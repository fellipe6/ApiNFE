package br.com.nazasoftapinfe.controller;


import br.com.nazasoftapinfe.entitiy.PessoaFisica;
import br.com.nazasoftapinfe.service.PessoaFisicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pessoaFisica")
public class PessoaFisicaController {

    private final PessoaFisicaService pessoaFisicaService;

    public PessoaFisicaController(PessoaFisicaService pessoaFisicaService) {
        this.pessoaFisicaService = pessoaFisicaService;
    }

    @GetMapping("/buscar/{cpf}")
    public ResponseEntity<PessoaFisica> buscarPessoaFisica(@PathVariable String cpf) {
        System.out.println("🔍 Recebendo requisição para CPF: " + cpf); // Log simples
        PessoaFisica pessoaFisica = pessoaFisicaService.buscarEGravarPessoaFisica(cpf);

        if (pessoaFisica != null) {
            return ResponseEntity.ok(pessoaFisica);
        }
        System.out.println("⚠️ Pessoa Física não encontrada no banco.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


}
