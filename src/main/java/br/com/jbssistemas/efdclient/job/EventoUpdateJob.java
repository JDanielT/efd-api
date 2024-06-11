package br.com.jbssistemas.efdclient.job;

import br.com.jbssistemas.efdclient.model.CertData;
import br.com.jbssistemas.efdclient.model.Declarante;
import br.com.jbssistemas.efdclient.repository.EventoRepository;
import br.com.jbssistemas.efdclient.service.ReinfApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

import static br.com.jbssistemas.efdclient.util.XmlUtils.elementToString;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class EventoUpdateJob {

    private final EventoRepository eventoRepository;
    private final ReinfApiService reinfApiService;

    public void updateEvt4010(Declarante declarante) {

        var tipoEvento = "R-4020-evt4020PagtoBeneficiarioPJ";
        var certData = CertData.builder()
                .alias(declarante.getCertAlias())
                .keystorePassword(declarante.getKeystorePassword())
                .keystorePath(declarante.getKeystorePath())
                .build();

        var eventos = eventoRepository.findByDeclaranteIdAndTipoEventoAndReferencia(declarante.getId(), tipoEvento, "2024-03");
        eventos.forEach(e -> {

            var response = reinfApiService.queryEvent(e.getProtocoloEnvio(), certData);
            e.setObservacao(getObservacoes(response));

            var element = response.getRetornoLoteEventosAssincrono()
                    .getRetornoEventos()
                    .getEvento()
                    .get(0)
                    .getRetornoEvento()
                    .getAny();

            var ideAdic = getIdentificadorAdicional(element);
            e.setIdentificadorAdicional(ideAdic);

            eventoRepository.save(e);
            eventoRepository.flush();

        });

    }

    public void updateEvt4020(Declarante declarante) {

        var tipoEvento = "R-4020-evt4020PagtoBeneficiarioPJ";
        var certData = CertData.builder()
                .alias(declarante.getCertAlias())
                .keystorePassword(declarante.getKeystorePassword())
                .keystorePath(declarante.getKeystorePath())
                .build();

        var eventos = eventoRepository.findByDeclaranteIdAndTipoEvento(declarante.getId(), tipoEvento);
        eventos.forEach(e -> {

            var response = reinfApiService.queryEvent(e.getProtocoloEnvio(), certData);
            e.setObservacao(getObservacoes(response));

            var element = response.getRetornoLoteEventosAssincrono()
                    .getRetornoEventos()
                    .getEvento()
                    .get(0)
                    .getRetornoEvento()
                    .getAny();

            var ideAdic = getIdentificadorAdicional(element);
            e.setIdentificadorAdicional(ideAdic);

            eventoRepository.save(e);
            eventoRepository.flush();

        });

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

    private static String getIdentificadorAdicional(Element element) {
        try {
            var xml = elementToString(element);
            var factory = DocumentBuilderFactory.newInstance();
            var builder = factory.newDocumentBuilder();
            var document = builder.parse(new InputSource(new StringReader(xml)));
            var ideEvtAdicList = document.getElementsByTagName("ideEvtAdic");
            if (ideEvtAdicList.getLength() > 0) {
                var ideEvtAdicElement = (Element) ideEvtAdicList.item(0);
                return ideEvtAdicElement.getTextContent();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
