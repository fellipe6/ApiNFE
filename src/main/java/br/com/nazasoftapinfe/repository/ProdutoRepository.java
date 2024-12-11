package br.com.nazasoftapinfe.repository;

import br.com.nazasoftapinfe.entitiy.Fornecedor;
import br.com.nazasoftapinfe.entitiy.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
  //  Optional<Fornecedor> findByCnpj(String cnpj);
}
