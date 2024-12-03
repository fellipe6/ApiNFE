package br.com.nazasoftapinfe.repository;

import br.com.nazasoftapinfe.entitiy.Emit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmitRepository  extends JpaRepository<Emit, Long> {

}
