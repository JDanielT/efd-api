package br.com.jbssistemas.efdclient.response;

import br.gov.esocial.reinf.schemas.retornoloteeventosassincrono.v1_00_00.Reinf;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

@Component
public class ReinfResponseParser {

    public Reinf parseXML(String xmlString) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Reinf.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader reader = new StringReader(xmlString);
        return (Reinf) unmarshaller.unmarshal(reader);
    }

}
