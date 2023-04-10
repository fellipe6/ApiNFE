package br.com.nazasoftapinfe.service;

import br.com.nazasoftapinfe.entitiy.Empresa;
import br.com.nazasoftapinfe.entitiy.NotaEntrada;
import br.com.nazasoftapinfe.exception.IntegracaoException;
import br.com.nazasoftapinfe.repository.NotaEntradaRepository;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import br.com.swconsultoria.nfe.exception.NfeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class NotaEntradaService {
    private final NotaEntradaRepository notaEntradaRepository;

    public NotaEntradaService(NotaEntradaRepository notaEntradaRepository) {
        this.notaEntradaRepository = notaEntradaRepository;

    }

    public void salvar(List<NotaEntrada> notasEntrada) {

             notaEntradaRepository.saveAll(notasEntrada);
    }

    public List<NotaEntrada> listarNotas(){
        return notaEntradaRepository.findAll();
    }
    public NotaEntrada listarPorID(Long idNota){
        return notaEntradaRepository.findById(idNota)
                .orElseThrow(()-> new IntegracaoException("Nota não encontrada com o id: " + idNota));
    }


    }

