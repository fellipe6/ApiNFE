package br.com.nazasoftapinfe.repository;


import br.com.nazasoftapinfe.entitiy.NotaEntrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotaEntradaRepository extends JpaRepository<NotaEntrada, Long> {
}
