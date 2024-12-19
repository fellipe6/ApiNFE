package br.com.nazasoftapinfe.service;

import br.com.nazasoftapinfe.entitiy.*;
import br.com.nazasoftapinfe.exception.IntegracaoException;
import br.com.nazasoftapinfe.repository.EmpresaRepository;
import br.com.nazasoftapinfe.util.ArquivoUtil;
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import br.com.swconsultoria.nfe.Nfe;
import br.com.swconsultoria.nfe.dom.ConfiguracoesNfe;
import br.com.swconsultoria.nfe.dom.Evento;
import br.com.swconsultoria.nfe.dom.enuns.*;
import br.com.swconsultoria.nfe.exception.NfeException;
import br.com.swconsultoria.nfe.schema.envConfRecebto.TEnvEvento;
import br.com.swconsultoria.nfe.schema.envConfRecebto.TRetEnvEvento;
import br.com.swconsultoria.nfe.schema.resnfe.ResNFe;
import br.com.swconsultoria.nfe.schema.retdistdfeint.RetDistDFeInt;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNFe;
import br.com.swconsultoria.nfe.schema_4.enviNFe.TNfeProc;
import br.com.swconsultoria.nfe.util.ManifestacaoUtil;
import br.com.swconsultoria.nfe.util.ObjetoUtil;
import br.com.swconsultoria.nfe.util.XmlNfeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DistribuicaoService {

    private final EmpresaRepository empresaRepository;
    private final NotaEntradaService notaEntradaService;
    private final FornecedorService fornecedorService;
    private final ProdutoService produtoService;

    public DistribuicaoService(EmpresaRepository empresaRepository, NotaEntradaService notaEntradaService, FornecedorService fornecedorService, ProdutoService produtoService) {
        this.empresaRepository = empresaRepository;
        this.notaEntradaService = notaEntradaService;
        this.fornecedorService = fornecedorService;

        this.produtoService = produtoService;
    }

    public void consultaNotas() throws CertificadoException, NfeException, IOException, JAXBException {
        List<Empresa> empresas = empresaRepository.findAll();
        for(Empresa empresa:empresas) {
            efetuaConsultaNotas(empresa);
        }
    }
    private void efetuaConsultaNotas(Empresa empresa) throws CertificadoException, NfeException, IOException, JAXBException {


        ConfiguracoesNfe configuracao = criaConfiguracao(empresa);
        List<String> listaNotasManifestar = new ArrayList<>();
        List<NotaEntrada> listasNotasSalvar = new ArrayList<>();
        List<Fornecedor> listasFornecedor = new ArrayList<>();

        boolean existeMais = true;
        while (existeMais) {
            RetDistDFeInt retorno = Nfe.distribuicaoDfe(configuracao, PessoaEnum.JURIDICA, empresa.getCpfCnpj(),
                    ConsultaDFeEnum.NSU, ObjetoUtil.verifica(empresa.getNsu()).orElse("000000000000000"));
            if (!retorno.getCStat().equals(StatusEnum.DOC_LOCALIZADO_PARA_DESTINATARIO.getCodigo())) {
                if (retorno.getCStat().equals(StatusEnum.CONSUMO_INDEVIDO.getCodigo())) {
                    break;
                } else {
                    throw new IntegracaoException("Erro ao pesquisar notas!" + retorno.getCStat() + "--" + retorno.getXMotivo());
                }
            }

            populaLista(empresa, listasFornecedor, listaNotasManifestar, listasNotasSalvar, retorno);
            existeMais = !retorno.getUltNSU().equals(retorno.getMaxNSU());
            empresa.setNsu(retorno.getUltNSU());
        }
        empresaRepository.save(empresa);
        notaEntradaService.salvar(listasNotasSalvar);
        fornecedorService.salvar(listasFornecedor);

        manifestaListaNotas(listaNotasManifestar,empresa,configuracao);
    }

    private void populaLista(Empresa empresa, List<Fornecedor> listasFornecedor,List<String> listaNotasManifestar,List<NotaEntrada> listasNotasSalvar, RetDistDFeInt retorno) throws JAXBException, IOException {

        for (RetDistDFeInt.LoteDistDFeInt.DocZip doc : retorno.getLoteDistDFeInt().getDocZip()) {
            String xml = XmlNfeUtil.gZipToXml(doc.getValue());
            log.info("XML: " + xml);
            log.info("XML" + doc.getSchema());
            log.info("NSU" + doc.getNSU());

            switch (doc.getSchema()) {
                case "resNFe_v1.01.xsd":
                    ResNFe resNFe = XmlNfeUtil.xmlToObject(xml, ResNFe.class);
                    String chave = resNFe.getChNFe();
                    listaNotasManifestar.add(chave);
                    break;
                case "procNFe_v4.00.xsd":
                    TNfeProc nfe = XmlNfeUtil.xmlToObject(xml, TNfeProc.class);
                    NotaEntrada notaEntrada = new NotaEntrada();
                    notaEntrada.setChave(nfe.getNFe().getInfNFe().getId().substring(3));
                    notaEntrada.setEmpresa(empresa);
                    notaEntrada.setDoc_schema(doc.getSchema());
                    notaEntrada.setCnpjEmitente(nfe.getNFe().getInfNFe().getEmit().getCNPJ());
                    notaEntrada.setNomeEmitente(nfe.getNFe().getInfNFe().getEmit().getXNome());
                    notaEntrada.setValor(new BigDecimal(nfe.getNFe().getInfNFe().getTotal().getICMSTot().getVNF()));
                    notaEntrada.setSerie(nfe.getNFe().getInfNFe().getIde().getSerie());
                    notaEntrada.setNumeroNota(nfe.getNFe().getInfNFe().getIde().getNNF());
                    notaEntrada.setDtEmit(nfe.getNFe().getInfNFe().getIde().getDhEmi());
                    notaEntrada.setXmlStr(xml);
                    notaEntrada.setXml(ArquivoUtil.compactaXml(xml));
                    //fornecedor
                    Fornecedor fornecedor = new Fornecedor();
                    fornecedor.setEmpresa(empresa);
                    fornecedor.setCnpj(nfe.getNFe().getInfNFe().getEmit().getCNPJ());
                    fornecedor.setNome(nfe.getNFe().getInfNFe().getEmit().getXNome().toUpperCase().toUpperCase());
                    fornecedor.setLogradouro(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXLgr().toUpperCase());
                    fornecedor.setNumero(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getNro());
                    fornecedor.setXCpl(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXCpl()!= null ? nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXCpl().toUpperCase() :"");
                    fornecedor.setXBairro(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXBairro().toUpperCase());
                    fornecedor.setCMun(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getCMun().toUpperCase());
                    fornecedor.setXMun(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXMun());
                    fornecedor.setUF(String.valueOf(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getUF()));
                    fornecedor.setCEP(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getCEP());
                    fornecedor.setCPais(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getCPais());
                    fornecedor.setXPais(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXPais()!= null ? nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXPais():"1058");
                    fornecedor.setIE(nfe.getNFe().getInfNFe().getEmit().getIE());
                    fornecedor.setCrt(nfe.getNFe().getInfNFe().getEmit().getCRT());

                    //produtos
                    List<TNFe.InfNFe.Det> det = nfe.getNFe().getInfNFe().getDet();

                    if (det != null && !det.isEmpty()) {
                        for (TNFe.InfNFe.Det detalhe : det) {
                            if (detalhe != null && detalhe.getProd() != null) {
                                Produto produto = new Produto();
                                produto.setCProd(detalhe.getProd().getCProd());
                                produto.setCEAN(detalhe.getProd().getCEAN());
                                produto.setChave(nfe.getNFe().getInfNFe().getId().substring(3));
                                produto.setVProd(new BigDecimal(detalhe.getProd().getVProd())); // Corrigido: campo correto para valor do produto.
                                produto.setXProd(detalhe.getProd().getXProd().toUpperCase());
                                produto.setCest(detalhe.getProd().getCEST());
                                produto.setCfop(detalhe.getProd().getCFOP());
                                produto.setUcom(detalhe.getProd().getUCom());
                                produto.setQcom(detalhe.getProd().getQCom());
                                produto.setVUnCom(detalhe.getProd().getVUnCom());
                                produto.setCEANTrib(detalhe.getProd().getCEANTrib());
                                produto.setUTrib(detalhe.getProd().getUTrib());
                                produto.setVUnTrib(detalhe.getProd().getVUnTrib());
                                produto.setQTrib(detalhe.getProd().getQTrib());
                                produto.setVUnCom(detalhe.getProd().getVUnCom());
                                produto.setNcm(detalhe.getProd().getNCM());
                                // Verificar se a lista Rastro não é nula e contém elementos
                                if (detalhe.getProd().getRastro() != null && !detalhe.getProd().getRastro().isEmpty()) {
                                    produto.setNLote(detalhe.getProd().getRastro().get(0).getNLote());
                                    produto.setQLote(detalhe.getProd().getRastro().get(0).getQLote());
                                    produto.setDFab(detalhe.getProd().getRastro().get(0).getDFab());
                                    produto.setDVal(detalhe.getProd().getRastro().get(0).getDVal());
                                } else {
                                    System.err.println("Rastro nulo ou vazio no produto: " + detalhe.getProd().getCProd());
                                }

                                try {
                                    // Salvar o produto no serviço
                                    produtoService.salvarProduto(produto);
                                } catch (Exception e) {
                                    System.err.println("Erro ao salvar o produto: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            } else {
                                System.err.println("Detalhe ou produto nulo encontrado.");
                            }
                        }
                    } else {
                        System.err.println("Lista de detalhes nula ou vazia.");
                    }


                    listasNotasSalvar.add(notaEntrada);
                    listasFornecedor.add(fornecedor);

                default:
                    break;
            }
        }
    }
    private void manifestaListaNotas(List<String> chaves,Empresa empresa,ConfiguracoesNfe configuracoesNfe) throws NfeException {

        for(String chave : chaves){

            Evento manifesta = new Evento();
            manifesta.setChave(chave);
            manifesta.setCnpj(empresa.getCpfCnpj());
            manifesta.setMotivo("Manifestação notas resumo");
            manifesta.setDataEvento(LocalDateTime.now());
            manifesta.setTipoManifestacao(ManifestacaoEnum.CIENCIA_DA_OPERACAO);


            TEnvEvento enviEvento = ManifestacaoUtil.montaManifestacao(manifesta, configuracoesNfe);
            TRetEnvEvento retorno = Nfe.manifestacao(configuracoesNfe, enviEvento, false);
            if(!retorno.getRetEvento().get(0).getInfEvento().getCStat().equals(StatusEnum.EVENTO_VINCULADO.getCodigo())){
                //if(!retorno.getCStat().equals(StatusEnum.EVENTO_VINCULADO)){
                log.error("Erro ao manifestar Chave: " + chave + ": "+retorno.getCStat()+"-"+retorno.getXMotivo());
            }
        }

    }
    private ConfiguracoesNfe criaConfiguracao(Empresa empresa) throws CertificadoException{

        Certificado certificado  = CertificadoService.certificadoPfxBytes(empresa.getCertificado(), empresa.getSenhaCertificado());

        return  ConfiguracoesNfe.criarConfiguracoes(
                EstadosEnum.valueOf(empresa.getUf()),
                empresa.getAmbiente(),
                certificado,
                "c:/certificado/schemas");
    }


}
