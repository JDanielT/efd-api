package br.com.jbssistemas.efdclient.util;

import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import java.io.StringReader;
import java.io.StringWriter;

@Log4j2
public class XmlUtils {

    public static Document parseXmlStringToDocument(String xmlString) {
        try {
            var factory = DocumentBuilderFactory.newInstance();
            var builder = factory.newDocumentBuilder();
            var inputSource = new InputSource(new StringReader(xmlString));
            return builder.parse(inputSource);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static Document parseObjectToDocument(Object obj, Class<?> clazz) {
        try {
            var res = new DOMResult();
            var context = JAXBContext.newInstance(clazz);
            context.createMarshaller().marshal(obj, res);
            return (Document) res.getNode();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static String convertObjectToXmlString(Object obj) {
        try {
            var jaxbContext = JAXBContext.newInstance(obj.getClass());
            var marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            var sw = new StringWriter();
            marshaller.marshal(obj, sw);
            return sw.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static String elementToString(Element element) {
        try {
            javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = tf.newTransformer();
            java.io.StringWriter writer = new java.io.StringWriter();
            transformer.transform(new javax.xml.transform.dom.DOMSource(element), new javax.xml.transform.stream.StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
