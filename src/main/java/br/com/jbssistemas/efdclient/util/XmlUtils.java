package br.com.jbssistemas.efdclient.util;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class XmlUtils {

    public static Document parseXmlString(String xmlString) throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var inputSource = new InputSource(new StringReader(xmlString));
        return builder.parse(inputSource);
    }

}
