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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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
    private final LogService logService;

    public DistribuicaoService(EmpresaRepository empresaRepository, NotaEntradaService notaEntradaService,
                               FornecedorService fornecedorService, ProdutoService produtoService, LogService logService) {
        this.empresaRepository = empresaRepository;
        this.notaEntradaService = notaEntradaService;
        this.fornecedorService = fornecedorService;
        this.produtoService = produtoService;
        this.logService = logService;
    }

    public void consultaNotas() throws CertificadoException, NfeException, IOException, JAXBException {
        List<Empresa> empresas = empresaRepository.findAll();
        for (Empresa empresa : empresas) {
            efetuaConsultaNotas(empresa);
        }
    }

    private void efetuaConsultaNotas(Empresa empresa) throws CertificadoException, NfeException, IOException, JAXBException {
        validarEmpresa(empresa);

        ConfiguracoesNfe configuracao = criaConfiguracao(empresa);
        validarConfiguracao(configuracao);

        List<String> listaNotasManifestar = new ArrayList<>();
        List<NotaEntrada> listasNotasSalvar = new ArrayList<>();
        List<Fornecedor> listasFornecedor = new ArrayList<>();

        boolean existeMais = true;
        while (existeMais) {
            RetDistDFeInt retorno = consultarNotas(empresa, configuracao);
            validarRetorno(retorno);

            logInfoConsulta(empresa, configuracao, retorno);

            if (!StatusEnum.DOC_LOCALIZADO_PARA_DESTINATARIO.getCodigo().equals(retorno.getCStat())) {
                tratarErroConsulta(retorno);
                break;
            }

            populaLista(empresa, listasFornecedor, listaNotasManifestar, listasNotasSalvar, retorno);

            existeMais = atualizarNSU(empresa, retorno);
        }

        salvarDados(empresa, listasNotasSalvar, listasFornecedor);
        manifestaListaNotas(listaNotasManifestar, empresa, configuracao);
    }

    private void validarEmpresa(Empresa empresa) {
        if (empresa == null) {
            throw new IllegalArgumentException("Empresa não pode ser null.");
        }
    }

    private void validarConfiguracao(ConfiguracoesNfe configuracao) {
        if (configuracao == null || configuracao.getCertificado() == null) {
            throw new IllegalStateException("Configuração ou certificado não pode ser null.");
        }
    }

    private RetDistDFeInt consultarNotas(Empresa empresa, ConfiguracoesNfe configuracao) throws NfeException {
        String nsu = ObjetoUtil.verifica(empresa.getNsu()).orElse("000000000000000");
        return Nfe.distribuicaoDfe(configuracao, PessoaEnum.JURIDICA, empresa.getCpfCnpj(), ConsultaDFeEnum.NSU, nsu);
    }

    private void validarRetorno(RetDistDFeInt retorno) {
        if (retorno == null) {
            throw new IntegracaoException("Retorno da consulta de notas é null.");
        }
    }

    private void logInfoConsulta(Empresa empresa, ConfiguracoesNfe configuracao, RetDistDFeInt retorno) {
        String msg = String.format("Iniciando busca: Tipo Pessoa: %s, Empresa: %s, CNPJ: %s, NSU: %s, Vencimento do certificado: %s",
                PessoaEnum.JURIDICA, empresa.getRazaoSocial(), empresa.getCpfCnpj(),
                ObjetoUtil.verifica(empresa.getNsu()).orElse("NSU não informado"),
                configuracao.getCertificado().getDataHoraVencimento());
        log.info(msg);
        logService.salvarLog("INFO", msg, null, null);
    }

    private void tratarErroConsulta(RetDistDFeInt retorno) {
        if (StatusEnum.CONSUMO_INDEVIDO.getCodigo().equals(retorno.getCStat())) {
            String msgCons = String.format("DESCRICAO DE ERRO: %s, CODIGO ERRO: %s", retorno.getXMotivo(), retorno.getCStat());
            logService.salvarLog("ERROR", msgCons, null, null);
        } else {
            throw new IntegracaoException("Erro ao pesquisar notas! " + retorno.getCStat() + " -- " + retorno.getXMotivo());
        }
    }

    private boolean atualizarNSU(Empresa empresa, RetDistDFeInt retorno) {
        if (retorno.getUltNSU() == null || retorno.getMaxNSU() == null) {
            throw new IntegracaoException("UltNSU ou MaxNSU não pode ser null.");
        }
        empresa.setNsu(retorno.getUltNSU());
        return !retorno.getUltNSU().equals(retorno.getMaxNSU());
    }

    private void salvarDados(Empresa empresa, List<NotaEntrada> listasNotasSalvar, List<Fornecedor> listasFornecedor) {
        empresaRepository.save(empresa);
        notaEntradaService.salvar(listasNotasSalvar);
        fornecedorService.salvar(listasFornecedor);
    }

    private void populaLista(Empresa empresa, List<Fornecedor> listasFornecedor, List<String> listaNotasManifestar,
                             List<NotaEntrada> listasNotasSalvar, RetDistDFeInt retorno) throws JAXBException, IOException {
        for (RetDistDFeInt.LoteDistDFeInt.DocZip doc : retorno.getLoteDistDFeInt().getDocZip()) {
            processarDocumento(empresa, listasFornecedor, listaNotasManifestar, listasNotasSalvar, doc);
        }
    }

    private void processarDocumento(Empresa empresa, List<Fornecedor> listasFornecedor, List<String> listaNotasManifestar,
                                    List<NotaEntrada> listasNotasSalvar, RetDistDFeInt.LoteDistDFeInt.DocZip doc) throws JAXBException, IOException {
        String xml = XmlNfeUtil.gZipToXml(doc.getValue());
        log.info("XML: {}, Schema: {}, NSU: {}", xml, doc.getSchema(), doc.getNSU());

        String msg = String.format("Manifestando notas - NSU: %s, Empresa: %s", doc.getNSU(), empresa.getRazaoSocial());
        log.info(msg);
        logService.salvarLog("INFO", msg, null, null);

        switch (doc.getSchema()) {
            case "resNFe_v1.01.xsd":
                processarResNFe(xml, listaNotasManifestar);
                break;
            case "procNFe_v4.00.xsd":
                processarProcNFe(empresa, listasFornecedor, listasNotasSalvar, doc, xml);
                break;
            default:
                log.warn("Schema não suportado: {}", doc.getSchema());
                break;
        }
    }

    private void processarResNFe(String xml, List<String> listaNotasManifestar) throws JAXBException {
        ResNFe resNFe = XmlNfeUtil.xmlToObject(xml, ResNFe.class);
        listaNotasManifestar.add(resNFe.getChNFe());
    }

    private void processarProcNFe(Empresa empresa, List<Fornecedor> listasFornecedor,
                                  List<NotaEntrada> listasNotasSalvar, RetDistDFeInt.LoteDistDFeInt.DocZip doc, String xml) throws JAXBException, IOException {
        TNfeProc nfe = XmlNfeUtil.xmlToObject(xml, TNfeProc.class);
        String chaveNota = nfe.getNFe().getInfNFe().getId().substring(3);
        String cnpjDestino = nfe.getNFe().getInfNFe().getDest().getCNPJ();

        salvarXmlNoServidor(xml, chaveNota, cnpjDestino);

        if (notaEntradaService.existeChave(chaveNota)) {
            log.info("Chave já existe na tabela NotaEntrada: {}", chaveNota);
            logService.salvarLog("INFO", "Chave já existe na tabela NotaEntrada: " + chaveNota, null, null);
            return;
        }

        NotaEntrada notaEntrada = criarNotaEntrada(empresa, doc, nfe, xml);
        listasNotasSalvar.add(notaEntrada);

        Fornecedor fornecedor = criarFornecedor(empresa, nfe, cnpjDestino);
        listasFornecedor.add(fornecedor);

        processarProdutos(nfe, chaveNota, cnpjDestino);
    }

    private NotaEntrada criarNotaEntrada(Empresa empresa, RetDistDFeInt.LoteDistDFeInt.DocZip doc, TNfeProc nfe, String xml) {
        NotaEntrada notaEntrada = new NotaEntrada();
        notaEntrada.setEmpresa(empresa);
        notaEntrada.setDoc_schema(doc.getSchema());
        notaEntrada.setCnpjEmitente(nfe.getNFe().getInfNFe().getEmit().getCNPJ());
        notaEntrada.setChave(nfe.getNFe().getInfNFe().getId().substring(3));
        notaEntrada.setNomeEmitente(nfe.getNFe().getInfNFe().getEmit().getXNome());
        notaEntrada.setValor(new BigDecimal(nfe.getNFe().getInfNFe().getTotal().getICMSTot().getVNF()));
        notaEntrada.setSerie(nfe.getNFe().getInfNFe().getIde().getSerie());
        notaEntrada.setNumeroNota(nfe.getNFe().getInfNFe().getIde().getNNF());
        notaEntrada.setDtEmit(nfe.getNFe().getInfNFe().getIde().getDhEmi());

        try {
            // Compacta o XML e define no objeto NotaEntrada
            byte[] xmlCompactado = ArquivoUtil.compactaXml(xml);
            notaEntrada.setXml(xmlCompactado);
        } catch (Exception e) {
            log.error("Erro ao compactar XML: {}", e.getMessage(), e);
            logService.salvarLog("ERROR", "Erro ao compactar XML: " + e.getMessage(), null, null);
            throw new RuntimeException("Erro ao compactar XML", e);
        }

        notaEntrada.setCNPJDestino(nfe.getNFe().getInfNFe().getDest().getCNPJ());
        return notaEntrada;
    }

    private Fornecedor criarFornecedor(Empresa empresa, TNfeProc nfe, String cnpjDestino) {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setEmpresa(empresa);
        fornecedor.setCnpj(nfe.getNFe().getInfNFe().getEmit().getCNPJ());
        fornecedor.setNome(nfe.getNFe().getInfNFe().getEmit().getXNome().toUpperCase());
        fornecedor.setLogradouro(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXLgr().toUpperCase());
        fornecedor.setNumero(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getNro());
        fornecedor.setXCpl(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXCpl() != null ?
                nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXCpl().toUpperCase() : "");
        fornecedor.setXBairro(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXBairro().toUpperCase());
        fornecedor.setCMun(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getCMun().toUpperCase());
        fornecedor.setXMun(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXMun());
        fornecedor.setUF(String.valueOf(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getUF()));
        fornecedor.setCEP(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getCEP());
        fornecedor.setCPais(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getCPais());
        fornecedor.setXPais(nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXPais() != null ?
                nfe.getNFe().getInfNFe().getEmit().getEnderEmit().getXPais() : "1058");
        fornecedor.setIE(nfe.getNFe().getInfNFe().getEmit().getIE());
        fornecedor.setCrt(nfe.getNFe().getInfNFe().getEmit().getCRT());
        fornecedor.setCNPJDestino(cnpjDestino);
        return fornecedor;
    }

    private void processarProdutos(TNfeProc nfe, String chaveNota, String cnpjDestino) {
        List<TNFe.InfNFe.Det> det = nfe.getNFe().getInfNFe().getDet();
        if (det == null || det.isEmpty()) {
            log.warn("Lista de detalhes nula ou vazia.");
            return;
        }

        for (TNFe.InfNFe.Det detalhe : det) {
            if (detalhe != null && detalhe.getProd() != null) {
                Produto produto = criarProduto(detalhe, chaveNota, cnpjDestino);
                salvarProduto(produto);
            } else {
                log.warn("Detalhe ou produto nulo encontrado.");
            }
        }
    }

    private Produto criarProduto(TNFe.InfNFe.Det detalhe, String chaveNota, String cnpjDestino) {
        Produto produto = new Produto();
        produto.setCProd(detalhe.getProd().getCProd());
        produto.setCEAN(detalhe.getProd().getCEAN());
        produto.setChave(chaveNota);
        produto.setVProd(new BigDecimal(detalhe.getProd().getVProd()));
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
        produto.setCNPJDestino(cnpjDestino);

        if (detalhe.getProd().getRastro() != null && !detalhe.getProd().getRastro().isEmpty()) {
            produto.setNLote(detalhe.getProd().getRastro().get(0).getNLote());
            produto.setQLote(detalhe.getProd().getRastro().get(0).getQLote());
            produto.setDFab(detalhe.getProd().getRastro().get(0).getDFab());
            produto.setDVal(detalhe.getProd().getRastro().get(0).getDVal());
        } else {
            log.warn("Rastro nulo ou vazio no produto: {}", detalhe.getProd().getCProd());
        }

        return produto;
    }

    private void salvarProduto(Produto produto) {
        try {
            produtoService.salvarProduto(produto);
            String msg = String.format("Produtos Salvos com sucesso! - Nome do produto: %s, CEAN: %s, Chave: %s",
                    produto.getXProd(), produto.getCEAN(), produto.getChave());
            log.info(msg);
            logService.salvarLog("INFO", msg, null, null);
        } catch (Exception e) {
            log.error("Erro ao salvar o produto: {}", e.getMessage(), e);
            logService.salvarLog("ERROR", "Erro ao salvar produtos!", e.getMessage(), null);
        }
    }

    private void manifestaListaNotas(List<String> chaves, Empresa empresa, ConfiguracoesNfe configuracoesNfe) throws NfeException {
        for (String chave : chaves) {
            Evento manifesta = new Evento();
            manifesta.setChave(chave);
            manifesta.setCnpj(empresa.getCpfCnpj());
            manifesta.setMotivo("Manifestação notas resumo");
            manifesta.setDataEvento(LocalDateTime.now());

            boolean deveManifestar = false; // Define com base na sua lógica

            if (deveManifestar) {
                manifesta.setTipoManifestacao(ManifestacaoEnum.CIENCIA_DA_OPERACAO);
            }

            // manifesta.setTipoManifestacao(ManifestacaoEnum.CIENCIA_DA_OPERACAO);

            TEnvEvento enviEvento = ManifestacaoUtil.montaManifestacao(manifesta, configuracoesNfe);
            TRetEnvEvento retorno = Nfe.manifestacao(configuracoesNfe, enviEvento, false);

            if (!StatusEnum.EVENTO_VINCULADO.getCodigo().equals(retorno.getRetEvento().get(0).getInfEvento().getCStat())) {
                log.error("Erro ao manifestar Chave: {}, CStat: {}, XMotivo: {}", chave, retorno.getCStat(), retorno.getXMotivo());
                logService.salvarLog("ERROR", "Erro ao manifestar Chave: " + chave + retorno.getCStat() + retorno.getXMotivo(), null, null);
            }
        }
    }

    private ConfiguracoesNfe criaConfiguracao(Empresa empresa) throws CertificadoException {
        Certificado certificado = CertificadoService.certificadoPfxBytes(empresa.getCertificado(), empresa.getSenhaCertificado());
        return ConfiguracoesNfe.criarConfiguracoes(
                EstadosEnum.valueOf(empresa.getUf()),
                empresa.getAmbiente(),
                certificado,
                "c:/certificado/schemas");
    }

    public void salvarXmlNoServidor(String xml, String chaveNota, String cnpjDestino) {
        try {
            if (xml == null || xml.isEmpty()) {
                log.error("❌ XML está vazio ou nulo.");
                return;
            }
            if (chaveNota == null || chaveNota.isEmpty()) {
                log.error("❌ Chave da nota está vazia ou nula.");
                return;
            }
            if (cnpjDestino == null || cnpjDestino.isEmpty()) {
                log.error("❌ CNPJ do destinatário está vazio ou nulo.");
                return;
            }

            // 📂 Diretório do XML
            String diretorioDestino = new File("").getAbsolutePath() + File.separator + "xmls" + File.separator + cnpjDestino;
            File pasta = new File(diretorioDestino);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            // 📝 Arquivo do XML
            File arquivoXml = new File(diretorioDestino + File.separator + chaveNota + ".xml");

            // 🔍 Verifica se o arquivo já existe antes de salvar
            if (arquivoXml.exists()) {
                log.info("⚠️ XML já salvo anteriormente: " + arquivoXml.getAbsolutePath());
                logService.salvarLog("INFO", "XML já salvo anteriormente: " + arquivoXml.getAbsolutePath(), null, xml);
                return;
            }

            // 💾 Salva o XML no servidor
            try (FileOutputStream fos = new FileOutputStream(arquivoXml)) {
                fos.write(xml.getBytes(StandardCharsets.UTF_8));
                log.info("✅ XML salvo com sucesso: " + arquivoXml.getAbsolutePath());
                logService.salvarLog("INFO", "XML salvo com sucesso: " + arquivoXml.getAbsolutePath(), null, null);
            }

        } catch (Exception e) {
            log.error("❌ Erro ao salvar XML: " + e.getMessage(), e);
            logService.salvarLog("ERROR", "Erro ao salvar XML da nota: "+ chaveNota  + " Erro:" +  e.getMessage(), String.valueOf(e), null);
        }
    }
}