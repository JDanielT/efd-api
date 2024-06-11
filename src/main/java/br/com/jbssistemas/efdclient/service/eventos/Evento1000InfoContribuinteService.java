package br.com.jbssistemas.efdclient.service.eventos;

import br.com.jbssistemas.efdclient.model.CertData;
import br.com.jbssistemas.efdclient.model.Declarante;
import br.com.jbssistemas.efdclient.model.Evento;
import br.com.jbssistemas.efdclient.repository.EventoRepository;
import br.com.jbssistemas.efdclient.service.ReinfApiService;
import br.com.jbssistemas.efdclient.service.SignerReinfDocumentService;
import br.com.jbssistemas.efdclient.service.WrapperReinfEventService;
import br.com.jbssistemas.efdclient.util.EventoUtil;
import br.gov.esocial.reinf.schemas.evtinfocontribuinte.v2_01_02.Reinf;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static br.com.jbssistemas.efdclient.util.XmlUtils.parseObjectToDocument;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class Evento1000InfoContribuinteService {

    private final ReinfApiService reinfApiService;
    private final SignerReinfDocumentService signerReinfDocumentService;
    private final WrapperReinfEventService wrapperReinfEventService;
    private final EventoRepository eventoRepository;

    public Evento enviar(Evento1000InfoContribuinteDto dto) throws Exception {

        var declarante = dto.getDeclarante();
        var raizDocDeclarante = declarante.getNumeroInscricao().substring(0, 8);
        var responsavel = dto.getResponsavelTecnico();
        var referencia = DateTimeFormatter.ofPattern("yyyy-MM").format(dto.getData());

        var certData = getCertData(declarante);

        var ideEvento = new Reinf.EvtInfoContri.IdeEvento();
        ideEvento.setTpAmb((short) 1);
        ideEvento.setProcEmi((short) 1);
        ideEvento.setVerProc("v1.0.0");

        var ideContri = new Reinf.EvtInfoContri.IdeContri();
        ideContri.setTpInsc((short) declarante.getTipoInscricao());
        ideContri.setNrInsc(raizDocDeclarante);

        var idePeriodo = new Reinf.EvtInfoContri.InfoContri.Inclusao.IdePeriodo();
        var iniValidad = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(referencia);
        idePeriodo.setIniValid(iniValidad);

        var infoCadastro = new Reinf.EvtInfoContri.InfoContri.Inclusao.InfoCadastro();
        infoCadastro.setClassTrib(declarante.getClassificacaoTributaria());

        // VALORES DEFAULT PARA MUNICIPIOS - TODO ADICIONAR CONFIG
        infoCadastro.setIndEscrituracao((short) 0);
        infoCadastro.setIndDesoneracao((short) 0);
        infoCadastro.setIndAcordoIsenMulta((short) 0);
        infoCadastro.setIndSitPJ((short) 0);

        var contato = new Reinf.EvtInfoContri.InfoContri.Inclusao.InfoCadastro.Contato();
        contato.setNmCtt(responsavel.getNome());
        contato.setCpfCtt(responsavel.getCpf());
        contato.setEmail(responsavel.getEmail());
        contato.setFoneCel(responsavel.getTelefone());

        infoCadastro.setContato(contato);

        var inclusao = new Reinf.EvtInfoContri.InfoContri.Inclusao();
        inclusao.setIdePeriodo(idePeriodo);
        inclusao.setInfoCadastro(infoCadastro);

        var infoContri = new Reinf.EvtInfoContri.InfoContri();
        infoContri.setInclusao(inclusao);

        var evtInfoContri = new Reinf.EvtInfoContri();

        var idEvento = EventoUtil.getEventoId(raizDocDeclarante, LocalDateTime.now());
        evtInfoContri.setId(idEvento);
        evtInfoContri.setIdeEvento(ideEvento);
        evtInfoContri.setIdeContri(ideContri);
        evtInfoContri.setInfoContri(infoContri);

        var eventoReinf = new Reinf();
        eventoReinf.setEvtInfoContri(evtInfoContri);

        var eventoAssinado = signerReinfDocumentService.sign(parseObjectToDocument(eventoReinf, Reinf.class), certData);

        var reinfEnvioLote = wrapperReinfEventService.wrapper(idEvento, eventoAssinado, declarante);
        var response = reinfApiService.postEvent(
                reinfEnvioLote,
                certData
        );

        var dadosRecepcao = response.getRetornoLoteEventosAssincrono().getDadosRecepcaoLote();

        var evento = Evento.builder()
                .id(idEvento)
                .referencia(referencia)
                .tipoEvento("R-1000-evtInfoContribuinte")
                .protocoloEnvio(dadosRecepcao == null ? "N/A" : dadosRecepcao.getProtocoloEnvio())
                .observacao(getObservacoes(response))
                .declarante(declarante)
                .build();

        return eventoRepository.save(evento);

    }

    private String getObservacoes(br.gov.esocial.reinf.schemas.retornoloteeventosassincrono.v1_00_00.Reinf response) {

        var status = response.getRetornoLoteEventosAssincrono().getStatus();

        var stringBuilder = new StringBuilder();
        stringBuilder.append(status.getCdResposta()).append(" - ").append(status.getDescResposta());

        var ocorrencias = status.getOcorrencias();
        if (nonNull(ocorrencias)) {
            ocorrencias.getOcorrencia().forEach(ocorrencia -> {
                stringBuilder.append("\n").append(ocorrencia.getCodigo()).append(" - ").append(ocorrencia.getDescricao());
            });
        }

        return stringBuilder.toString();

    }

    private CertData getCertData(Declarante declarante) {
        return CertData.builder()
                .alias(declarante.getCertAlias())
                .keystorePath(declarante.getKeystorePath())
                .keystorePassword(declarante.getKeystorePassword())
                .build();
    }

}
