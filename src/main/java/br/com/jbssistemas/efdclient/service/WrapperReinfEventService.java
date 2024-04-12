package br.com.jbssistemas.efdclient.service;

import br.com.jbssistemas.efdclient.model.Declarante;
import br.gov.esocial.reinf.schemas.envioloteeventosassincrono.v1_00_00.Reinf;
import br.gov.esocial.reinf.schemas.envioloteeventosassincrono.v1_00_00.TArquivoReinf;
import br.gov.esocial.reinf.schemas.envioloteeventosassincrono.v1_00_00.TIdeContribuinte;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

@Service
@RequiredArgsConstructor
public class WrapperReinfEventService {

    public Reinf wrapper(String idEvento, Document evento, Declarante declarante) {
        var tArquivoReinf = new TArquivoReinf();
        tArquivoReinf.setId(idEvento);
        tArquivoReinf.setAny(evento.getDocumentElement());

        var tIdeContribuinte = new TIdeContribuinte();
        tIdeContribuinte.setTpInsc((short) declarante.getTipoInscricao());
        tIdeContribuinte.setNrInsc(declarante.getNumeroInscricao());

        var envioLoteEventos = new Reinf.EnvioLoteEventos();

        envioLoteEventos.setIdeContribuinte(tIdeContribuinte);

        envioLoteEventos.setEventos(new Reinf.EnvioLoteEventos.Eventos());
        envioLoteEventos.getEventos().getEvento().add(tArquivoReinf);

        var reinfEnvioLote = new Reinf();
        reinfEnvioLote.setEnvioLoteEventos(envioLoteEventos);

        return reinfEnvioLote;

    }

}
