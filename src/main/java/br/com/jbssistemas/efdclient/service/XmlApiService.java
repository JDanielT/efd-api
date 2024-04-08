package br.com.jbssistemas.efdclient.service;

import br.com.jbssistemas.efdclient.model.CertData;
import br.com.jbssistemas.efdclient.response.ReinfResponseParser;
import br.com.jbssistemas.efdclient.util.XmlUtils;
import br.gov.esocial.reinf.schemas.retornoloteeventosassincrono.v1_00_00.Reinf;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class XmlApiService {

    private final KeyStoreService keyStoreService;
    private final SignerReinfDocumentService signerReinfDocumentService;
    private final WrapperReinfEventService wrapperReinfEventService;
    private final ReinfResponseParser reinfResponseParser;

    public Reinf sendSignedXmlData(String eventId, Object xmlObject, String apiUrl, CertData certData) {
        try {
            var xmlString = convertObjectToXml(xmlObject);
            var document = XmlUtils.parseXmlString(xmlString);
            var signedXml = signerReinfDocumentService.sign(document, certData);
            var keyStore = keyStoreService.getKeyStore(certData);

            TrustStrategy trustStrategy = (chain, authType) -> true;

            var sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(keyStore, certData.getKeystorePassword().toCharArray())
                    .loadTrustMaterial(trustStrategy)
                    .build();

            var socketFactory = new SSLConnectionSocketFactory(sslContext);

            try (var connectionManager = PoolingHttpClientConnectionManagerBuilder.create().setSSLSocketFactory(socketFactory).build();
                 var httpClient = HttpClients.custom().setConnectionManager(connectionManager).build()) {

                var wrapped = wrapperReinfEventService.wrapper(eventId, signedXml);

                var post = new HttpPost(apiUrl);
                post.setEntity(EntityBuilder.create().setText(wrapped).build());
                post.addHeader("Content-Type", "application/xml");

                try (var response = httpClient.execute(post)) {

                    var responseStream = response.getEntity().getContent();
                    var responseBody = new BufferedReader(new InputStreamReader(responseStream))
                            .lines().collect(Collectors.joining("\n"));

                    return reinfResponseParser.parseXML(responseBody);

                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String convertObjectToXml(Object obj) {
        try {
            var jaxbContext = JAXBContext.newInstance(obj.getClass());
            var marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            var sw = new StringWriter();
            marshaller.marshal(obj, sw);
            return sw.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
