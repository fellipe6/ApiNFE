package br.com.nazasoftapinfe.service;

import br.com.nazasoftapinfe.entitiy.Fornecedor;
import br.com.nazasoftapinfe.repository.FornecedorRepository;
import org.springframework.stereotype.Service;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    public Fornecedor cadastrarFornecedor(Fornecedor fornecedor) {
        // Verifica se o CNPJ já existe
        fornecedorRepository.findByCnpj(fornecedor.getCnpj())
                .ifPresent(e -> {
                    throw new IllegalArgumentException("CNPJ já cadastrado: " + fornecedor.getCnpj());
                });

        // Salva a empresa se não existir
        return fornecedorRepository.save(fornecedor);
    }
}
