package br.com.jbssistemas.efdclient.response.parser;

import br.gov.esocial.reinf.schemas.retornoloteeventosassincrono.v1_00_00.Reinf;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;

@Component
public class ReinfResponseParser {

    public Reinf parseXML(String xmlString) throws JAXBException {
        var jaxbContext = JAXBContext.newInstance(Reinf.class);
        var unmarshaller = jaxbContext.createUnmarshaller();
        var reader = new StringReader(xmlString);
        return (Reinf) unmarshaller.unmarshal(reader);
    }

}
