package br.com.nazasoftapinfe.service;

import br.com.nazasoftapinfe.entitiy.Fornecedor;
import br.com.nazasoftapinfe.entitiy.NotaEntrada;
import br.com.nazasoftapinfe.repository.FornecedorRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;

    }

    public void salvar(List<Fornecedor> fornecedores) {
        List<Fornecedor> novosFornecedores = new ArrayList<>();

        for (Fornecedor fornecedor : fornecedores) {
            // Verifica se o CNPJ já está cadastrado no banco
            boolean existe = fornecedorRepository.existsByCnpj(fornecedor.getCnpj());
            if (!existe) {
                novosFornecedores.add(fornecedor);
            } else {
                System.out.println("CNPJ já cadastrado: " + fornecedor.getCnpj());
            }
        }

        // Salva apenas os fornecedores com CNPJs não duplicados
        if (!novosFornecedores.isEmpty()) {
            fornecedorRepository.saveAll(novosFornecedores);
        }
    }

    public List<Fornecedor> listaFornecedor(){
        return fornecedorRepository.findAll();
    }

}
