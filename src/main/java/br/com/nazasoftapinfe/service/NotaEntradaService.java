package br.com.nazasoftapinfe.service;

import br.com.nazasoftapinfe.entitiy.NotaEntrada;
import br.com.nazasoftapinfe.exception.IntegracaoException;
import br.com.nazasoftapinfe.repository.NotaEntradaRepository;
import br.com.nazasoftapinfe.util.ArquivoUtil;

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
    public NotaEntrada listarPorChave(String chave){
        return notaEntradaRepository.findFirstByChave(chave)
                .orElseThrow(()-> new IntegracaoException("Nota não encontrada com a chave: " + chave));
    }

    public String getXml(String chave) throws IOException {
      NotaEntrada notaEntrada = listarPorChave(chave);
      return ArquivoUtil.descompactaXml(notaEntrada.getXml());
    }


    //todo Fazer a impressão da NFE
    public String geraImpressao(Long idNota) throws IOException {
        NotaEntrada notaEntrada = listarPorID(idNota);
        ArquivoUtil.descompactaXml(notaEntrada.getXml());
        return ArquivoUtil.descompactaXml(notaEntrada.getXml());
    }

    public NotaEntrada getPorChave(String chave) {
        return notaEntradaRepository.findFirstByChave(chave)
                .orElseThrow(()-> new IntegracaoException("Nota não encontrada com o chave: " + chave));
    }
    public boolean existeChave(String chave) {
        return notaEntradaRepository.existsByChave(chave);
    }
}

