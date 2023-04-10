package br.com.nazasoftapinfe.repository;

import br.com.nazasoftapinfe.entitiy.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}
