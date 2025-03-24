package br.com.nazasoftapinfe.repository;

import br.com.nazasoftapinfe.entitiy.PessoaFisica;
import org.springframework.data.jpa.repository.JpaRepository;

public
interface PessoaFisicaRepository extends JpaRepository<PessoaFisica, String> {}