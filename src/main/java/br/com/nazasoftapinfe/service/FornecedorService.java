package br.com.nazasoftapinfe.service;

import br.com.nazasoftapinfe.entitiy.Fornecedor;
import br.com.nazasoftapinfe.repository.FornecedorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final LogService logService;

    public FornecedorService(FornecedorRepository fornecedorRepository, LogService logService) {
        this.fornecedorRepository = fornecedorRepository;
        this.logService = logService;
    }

    public void salvar(List<Fornecedor> fornecedores) {
        if (fornecedores == null || fornecedores.isEmpty()) {
            log.warn("Lista de fornecedores está vazia, nula ou sem notas para importação no momento!");
            logService.salvarLog("WARN", "Lista de fornecedores está vazia, nula ou sem notas para importação no momento!", null,null);
            return;
        }

        List<Fornecedor> novosFornecedores = filtrarFornecedoresNaoDuplicados(fornecedores);

        if (!novosFornecedores.isEmpty()) {
            try {
                String msg = "Fornecedores salvos com sucesso: " + novosFornecedores.size();
                log.info(msg);
                logService.salvarLog("INFO", msg, null,null);
                fornecedorRepository.saveAll(novosFornecedores);

            } catch (Exception e) {
                log.error("Erro ao salvar fornecedores", e);
                logService.salvarLog("ERROR", "Erro ao salvar fornecedores", e.getMessage(),null);
            }
        } else {
            log.info("Nenhum fornecedor novo para salvar.");
            logService.salvarLog("INFO", "Nenhum fornecedor novo para salvar.", null,null);
        }
    }

    private List<Fornecedor> filtrarFornecedoresNaoDuplicados(List<Fornecedor> fornecedores) {
        List<Fornecedor> novosFornecedores = new ArrayList<>();

        for (Fornecedor fornecedor : fornecedores) {
            if (!isFornecedorDuplicado(fornecedor)) {
                novosFornecedores.add(fornecedor);
            } else {
                String msg = "CNPJ já cadastrado: " + fornecedor.getCnpj() + " para a empresa: " + fornecedor.getEmpresa();
                log.warn(msg);
                logService.salvarLog("WARN", msg, null,null);
            }
        }

        return novosFornecedores;
    }

    private boolean isFornecedorDuplicado(Fornecedor fornecedor) {
        return fornecedorRepository.existsByCnpjAndEmpresa(fornecedor.getCnpj(), fornecedor.getEmpresa());
    }

    public List<Fornecedor> listaFornecedor() {
        return fornecedorRepository.findAll();
    }
}
