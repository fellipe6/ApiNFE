package br.com.nazasoftapinfe.repository;

import br.com.nazasoftapinfe.entitiy.Empresa;
import br.com.nazasoftapinfe.entitiy.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    Optional<Fornecedor> findByCnpj(String cnpj);


    boolean existsByCnpjAndEmpresa(String cnpj, Empresa empresa);

    boolean existsByCnpjAndEmpresa(String cnpj, String empresa);

    @Query("SELECT f.cnpj FROM Fornecedor f WHERE f.empresa = :empresa")
    Set<String> findCnpjsByEmpresa(String empresa);

}
