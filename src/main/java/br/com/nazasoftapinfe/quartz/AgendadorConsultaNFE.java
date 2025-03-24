package br.com.nazasoftapinfe.quartz;

import br.com.nazasoftapinfe.controller.NotaEntradaController;
import br.com.nazasoftapinfe.service.DistribuicaoService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@DisallowConcurrentExecution
public class AgendadorConsultaNFE {

    private final DistribuicaoService distribuicaoService;

    private final NotaEntradaController notaEntradaController;

    public AgendadorConsultaNFE(DistribuicaoService distribuicaoService, NotaEntradaController notaEntradaController) {
        this.distribuicaoService = distribuicaoService;
        this.notaEntradaController = notaEntradaController;
    }
   // @Scheduled(initialDelay=1000*60*10,fixedDelay=7200000)//Serviço irá consultar na SEFAZ a cada uma hora
    public void efetuaConsulta(){
        try {
            log.info("Iniciando a operação de consultas!");
            notaEntradaController.consulta();
            log.info("Operação de consulta finalizada!");
        }catch (Exception e){
            log.error("Erro ao realizar a consulta de notas na SEFAZ!", e);
        }
    }
}
