package br.com.jbssistemas.efdclient.service;

import org.springframework.stereotype.Service;

@Service
public class WrapperReinfEventService {

    private static final String ROOT_XMLNS = "http://sped.fazenda.gov.br/";
    private static final String REINF_XMLNS = "http://www.reinf.esocial.gov.br/schemas/envioLoteEventosAssincrono/v1_00_00";

    public String wrapper(String eventId, String eventContent) {
        var sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        sb.append("<Reinf xmlns=\"" + REINF_XMLNS + "\">");
        sb.append("<envioLoteEventos>");
        sb.append("<ideContribuinte>");
        sb.append("<tpInsc>1</tpInsc>");
        sb.append("<nrInsc>07811946000187</nrInsc>");
        sb.append("</ideContribuinte>");
        sb.append("<eventos>");
        sb.append("<evento Id=\"" + eventId + "\">");
        sb.append(eventContent.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", ""));
        sb.append("</evento>");
        sb.append("</eventos>");
        sb.append("</envioLoteEventos>");
        sb.append("</Reinf>");
        return sb.toString();
    }

}
