package br.com.nazasoftapinfe.service;

import br.com.nazasoftapinfe.entitiy.Emit;
import br.com.nazasoftapinfe.entitiy.Empresa;
import br.com.nazasoftapinfe.repository.EmitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmitService {

    @Autowired
    private EmitRepository emitRepository;

    public Emit salvar(Emit emit) {
        log.info("Salvando Emitente");
        return emitRepository.save(emit);
    }
}
