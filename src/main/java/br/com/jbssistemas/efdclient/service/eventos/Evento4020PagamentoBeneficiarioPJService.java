package br.com.jbssistemas.efdclient.service.eventos;

import br.com.jbssistemas.efdclient.model.CertData;
import br.com.jbssistemas.efdclient.model.Declarante;
import br.com.jbssistemas.efdclient.model.Evento;
import br.com.jbssistemas.efdclient.repository.EventoRepository;
import br.com.jbssistemas.efdclient.service.ReinfApiService;
import br.com.jbssistemas.efdclient.service.SignerReinfDocumentService;
import br.com.jbssistemas.efdclient.service.WrapperReinfEventService;
import br.com.jbssistemas.efdclient.util.EventoUtil;
import br.gov.esocial.reinf.schemas.evt4020pagtobeneficiariopj.v2_01_02.Reinf;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeFactory;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static br.com.jbssistemas.efdclient.util.XmlUtils.parseObjectToDocument;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class Evento4020PagamentoBeneficiarioPJService {

    private final ReinfApiService reinfApiService;
    private final SignerReinfDocumentService signerReinfDocumentService;
    private final WrapperReinfEventService wrapperReinfEventService;
    private final EventoRepository eventoRepository;

    public Evento enviar(Evento4020PagamentoBeneficiarioPJDto dto) {

        var competencia = DateTimeFormatter.ofPattern("yyyy-MM").format(dto.getData());
        var declarante = dto.getDeclarante();
        var raizDocDeclarante = declarante.getNumeroInscricao().substring(0, 8);
        var certData = getCertData(declarante);

        var infoPgto = createInfoPgto(dto);
        var idePgto = createIdePgto(dto, infoPgto);
        var ideEvento = createIdeEvento(competencia);
        var ideContri = createIdeContri(declarante, raizDocDeclarante);
        var ideBenef = createIdeBenef(dto, idePgto);
        var ideEstab = createIdeEstab(declarante, ideBenef);

        var idEvento = EventoUtil.getEventoId(raizDocDeclarante, LocalDateTime.now());
        var evtRetPf = createEvtRefPF(idEvento, ideEvento, ideContri, ideEstab);

        var eventoReinf = new Reinf();
        eventoReinf.setEvtRetPJ(evtRetPf);

        var eventoAssinado = signerReinfDocumentService.sign(parseObjectToDocument(eventoReinf, Reinf.class), certData);

        var reinfEnvioLote = wrapperReinfEventService.wrapper(idEvento, eventoAssinado, declarante);
        var response = reinfApiService.postEvent(
                reinfEnvioLote,
                certData
        );

        var dadosRecepcao = response.getRetornoLoteEventosAssincrono().getDadosRecepcaoLote();

        var evento = Evento.builder()
                .id(idEvento)
                .referencia(competencia)
                .tipoEvento("R-4010-evt4010PagtoBeneficiarioPF")
                .protocoloEnvio(dadosRecepcao == null ? "N/A" : dadosRecepcao.getProtocoloEnvio())
                .observacao(getObservacoes(response))
                .declarante(declarante)
                .build();

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return eventoRepository.save(evento);

    }

    public Reinf.EvtRetPJ.IdeEvento createIdeEvento(String competencia) {
        try {
            var ideEvento = new Reinf.EvtRetPJ.IdeEvento();
            ideEvento.setIndRetif((short) 1);
            ideEvento.setPerApur(DatatypeFactory.newInstance().newXMLGregorianCalendar(competencia));
            ideEvento.setTpAmb((short) 1);
            ideEvento.setProcEmi((short) 1);
            ideEvento.setVerProc("v1.0.0");
            return ideEvento;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Reinf.EvtRetPJ.IdeContri createIdeContri(Declarante declarante, String raizDocDeclarante) {
        var ideContri = new Reinf.EvtRetPJ.IdeContri();
        ideContri.setTpInsc((short) declarante.getTipoInscricao());
        ideContri.setNrInsc(raizDocDeclarante);
        return ideContri;
    }

    public Reinf.EvtRetPJ.IdeEstab createIdeEstab(Declarante declarante, Reinf.EvtRetPJ.IdeEstab.IdeBenef ideBenef) {
        var ideEstab = new Reinf.EvtRetPJ.IdeEstab();
        ideEstab.setTpInscEstab((short) declarante.getTipoInscricao());
        ideEstab.setNrInscEstab(declarante.getNumeroInscricao());
        ideEstab.setIdeBenef(ideBenef);
        return ideEstab;
    }

    public Reinf.EvtRetPJ.IdeEstab.IdeBenef createIdeBenef(
            Evento4020PagamentoBeneficiarioPJDto dto,
            Reinf.EvtRetPJ.IdeEstab.IdeBenef.IdePgto idePgto
    ) {
        var ideBenef = new Reinf.EvtRetPJ.IdeEstab.IdeBenef();
        ideBenef.setCnpjBenef(dto.getCpfBeneficiario());
        if (isBlank(dto.getCpfBeneficiario())) {
            ideBenef.setNmBenef(dto.getNomeBeneficiario());
        }
        ideBenef.getIdePgto().add(idePgto);
        ideBenef.setIdeEvtAdic(dto.getNumeroPagamento());
        return ideBenef;
    }

    public Reinf.EvtRetPJ.IdeEstab.IdeBenef.IdePgto.InfoPgto createInfoPgto(
            Evento4020PagamentoBeneficiarioPJDto dto
    ) {
        try {

            var data = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dto.getData());

            var infoPgto = new Reinf.EvtRetPJ.IdeEstab.IdeBenef.IdePgto.InfoPgto();
            infoPgto.setDtFG(DatatypeFactory.newInstance().newXMLGregorianCalendar(data));
            infoPgto.setVlrBruto(formatarValor(dto.getValorRendimentoBruto()));

            var retencoes = new Reinf.EvtRetPJ.IdeEstab.IdeBenef.IdePgto.InfoPgto.Retencoes();
            retencoes.setVlrBaseIR(formatarValor(dto.getValorRendimentoTributavel()));
            retencoes.setVlrIR(formatarValor(dto.getValorIr()));

            infoPgto.setRetencoes(retencoes);

            return infoPgto;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Reinf.EvtRetPJ.IdeEstab.IdeBenef.IdePgto createIdePgto(
            Evento4020PagamentoBeneficiarioPJDto dto,
            Reinf.EvtRetPJ.IdeEstab.IdeBenef.IdePgto.InfoPgto infoPgto
    ) {
        var idePgto = new Reinf.EvtRetPJ.IdeEstab.IdeBenef.IdePgto();
        idePgto.setNatRend(dto.getNaturezaRendimento());
        idePgto.getInfoPgto().add(infoPgto);
        return idePgto;
    }

    private Reinf.EvtRetPJ createEvtRefPF(
            String idEvento,
            Reinf.EvtRetPJ.IdeEvento ideEvento,
            Reinf.EvtRetPJ.IdeContri ideContri,
            Reinf.EvtRetPJ.IdeEstab ideEstab
    ) {
        var evtRetPf = new Reinf.EvtRetPJ();
        evtRetPf.setId(idEvento);
        evtRetPf.setIdeEvento(ideEvento);
        evtRetPf.setIdeContri(ideContri);
        evtRetPf.setIdeEstab(ideEstab);
        return evtRetPf;
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

    private String formatarValor(BigDecimal valor) {

        if (BigDecimal.ZERO.equals(valor)) {
            return "0,00";
        }

        var symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
        symbols.setDecimalSeparator(',');

        var decimalFormat = new DecimalFormat("####.00", symbols);
        return decimalFormat.format(valor);
    }

}
