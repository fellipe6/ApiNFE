package br.com.nazasoftapinfe.service;

import br.com.nazasoftapinfe.entitiy.Fornecedor;
import br.com.nazasoftapinfe.entitiy.NotaEntrada;
import br.com.nazasoftapinfe.repository.FornecedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;

    }

    public void salvar(List<Fornecedor> fornecedor) {

        fornecedorRepository.saveAll(fornecedor);
    }

    public List<Fornecedor> listaFornecedor(){
        return fornecedorRepository.findAll();
    }

}
