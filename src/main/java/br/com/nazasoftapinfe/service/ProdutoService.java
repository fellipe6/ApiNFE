package br.com.nazasoftapinfe.service;

import br.com.nazasoftapinfe.entitiy.Produto;
import br.com.nazasoftapinfe.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {
    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto salvarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }
}
