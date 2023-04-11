package br.com.nazasoftapinfe.quartz;

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

    public AgendadorConsultaNFE(DistribuicaoService distribuicaoService) {
        this.distribuicaoService = distribuicaoService;
    }
    @Scheduled(initialDelay =  1000*60*10,fixedDelay = 3600000)
    public void efetuaConsulta(){
        try {
            log.info("Iniciando a operação de consultas!");
            distribuicaoService.consultaNotas();
            log.info("Operação de consulta finalizada!");
        }catch (Exception e){
            log.error("Erro ao realizar a consulta de notas na SEFAZ!", e);
        }
    }
}
