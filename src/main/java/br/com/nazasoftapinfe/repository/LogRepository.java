package br.com.nazasoftapinfe.repository;

import br.com.nazasoftapinfe.entitiy.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<LogEntry, Long> {
}