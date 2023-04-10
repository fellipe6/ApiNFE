package br.com.nazasoftapinfe.service;

import br.com.nazasoftapinfe.entitiy.Empresa;
import br.com.nazasoftapinfe.exception.IntegracaoException;
import br.com.nazasoftapinfe.repository.EmpresaRepository;
import br.com.swconsultoria.nfe.util.ObjetoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public Empresa salvar(Empresa empresa) {

        validar(empresa);
        log.info("Salvando Empresa");
        return empresaRepository.save(empresa);
    }

    public void deletar(Long idEmpresa){
        empresaRepository.deleteById(idEmpresa);
    }

    public List<Empresa> listarEmpresas(){
        return empresaRepository.findAll();
    }

    public Empresa listarPorID(Long idEmpresa){
        return empresaRepository.findById(idEmpresa)
                .orElseThrow(()-> new IntegracaoException("Empresa não encontrada com o id: " + idEmpresa));
    }
    private void validar(Empresa empresa){
        ObjetoUtil.verifica(empresa.getCpfCnpj()).orElseThrow(()->
         new IntegracaoException("Campo CPF/CNPJ obrigatório!")
        );
        ObjetoUtil.verifica(empresa.getCertificado()).orElseThrow(()->
        new IntegracaoException("Campo certificado obrigatório!")
        );
    }

}
