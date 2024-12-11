package br.com.nazasoftapinfe.service;

import br.com.nazasoftapinfe.entitiy.Empresa;
import br.com.nazasoftapinfe.entitiy.Fornecedor;
import br.com.nazasoftapinfe.entitiy.NotaEntrada;
import br.com.nazasoftapinfe.entitiy.Produto;
import br.com.nazasoftapinfe.exception.IntegracaoException;
import br.com.nazasoftapinfe.repository.EmpresaRepository;
import br.com.nazasoftapinfe.repository.ProdutoRepository;
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
import java.util.Optional;

@Slf4j
@Service
public class DistribuicaoServiceTeste {

    private final EmpresaRepository empresaRepository;
    private final NotaEntradaService notaEntradaService;
    private final FornecedorService fornecedorService;
    private final ProdutoService produtoService;

    public DistribuicaoServiceTeste(EmpresaRepository empresaRepository, NotaEntradaService notaEntradaService, FornecedorService fornecedorService, ProdutoRepository produtoRepository, ProdutoService produtoService) {
        this.empresaRepository = empresaRepository;
        this.notaEntradaService = notaEntradaService;
        this.fornecedorService = fornecedorService;
        this.produtoService = produtoService;

       // this.produtoRepository = produtoRepository;
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
                    fornecedor.setNome(nfe.getNFe().getInfNFe().getEmit().getXNome().toUpperCase());
                    fornecedor.setLogradouro(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXLgr());
                    fornecedor.setNumero(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getNro());
                    fornecedor.setXCpl(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXCpl());
                    fornecedor.setXBairro(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXBairro());
                    fornecedor.setCMun(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getCMun());
                    fornecedor.setXMun(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXMun());
                    fornecedor.setUF(String.valueOf(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getUF()));
                    fornecedor.setCEP(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getCEP());
                    fornecedor.setCPais(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getCPais());
                    fornecedor.setXPais(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXPais());
                    fornecedor.setIE(nfe.getNFe().getInfNFe().getEmit().getIE());
                    fornecedor.setCrt(nfe.getNFe().getInfNFe().getEmit().getCRT());
                    //
                    Produto produto = new Produto();
                    List<TNFe.InfNFe.Det> det = nfe.getNFe().getInfNFe().getDet();
                    if (det != null && !det.isEmpty()) {
                        produto.setCProd(det.get(0).getProd().getCProd());
                        produto.setCEAN(det.get(0).getProd().getCEAN());
                        produto.setChave(det.get(0).getProd().getCProd());
                        produto.setXProd(det.get(0).getProd().getXProd());
                        produtoService.salvarProduto(produto);

                    } else {
                        // Trate o caso onde a lista está vazia ou nula
                    }

                    //produto.setCProd(nfe.getNFe().getInfNFe().getDet().get(0).getProd().getCProd());
                  //  produto.setCProd(nfe.getVersao());*/
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
