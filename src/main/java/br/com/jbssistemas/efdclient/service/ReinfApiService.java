package br.com.jbssistemas.efdclient.service;

import br.com.jbssistemas.efdclient.model.CertData;
import br.com.jbssistemas.efdclient.response.parser.ReinfResponseParser;
import br.gov.esocial.reinf.schemas.retornoloteeventosassincrono.v1_00_00.Reinf;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static br.com.jbssistemas.efdclient.util.XmlUtils.convertObjectToXmlString;

@Service
@RequiredArgsConstructor
public class ReinfApiService {

    private final KeyStoreService keyStoreService;
    private final ReinfResponseParser reinfResponseParser;

    public Reinf postEvent(Object xmlObject, CertData certData) {
        try {

            var apiUrl = "https://reinf.receita.economia.gov.br/recepcao/lotes";

            var xmlString = convertObjectToXmlString(xmlObject);
            var keyStore = keyStoreService.getKeyStore(certData);

            TrustStrategy trustStrategy = (chain, authType) -> true;

            var sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(keyStore, certData.getKeystorePassword().toCharArray())
                    .loadTrustMaterial(trustStrategy)
                    .build();

            var socketFactory = new SSLConnectionSocketFactory(sslContext);

            try (var connectionManager = PoolingHttpClientConnectionManagerBuilder.create().setSSLSocketFactory(socketFactory).build();
                 var httpClient = HttpClients.custom().setConnectionManager(connectionManager).build()) {

                var post = new HttpPost(apiUrl);
                post.setEntity(EntityBuilder.create().setText(xmlString).build());
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

    public Reinf queryEvent(String protocol, CertData certData) {
        try {
            var apiUrl = "https://reinf.receita.economia.gov.br/consulta/lotes/" + protocol;
            var keyStore = keyStoreService.getKeyStore(certData);

            TrustStrategy trustStrategy = (chain, authType) -> true;

            var sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(keyStore, certData.getKeystorePassword().toCharArray())
                    .loadTrustMaterial(trustStrategy)
                    .build();

            var socketFactory = new SSLConnectionSocketFactory(sslContext);

            try (var connectionManager = PoolingHttpClientConnectionManagerBuilder.create().setSSLSocketFactory(socketFactory).build();
                 var httpClient = HttpClients.custom().setConnectionManager(connectionManager).build()) {

                var get = new HttpGet(apiUrl);
                get.addHeader("Content-Type", "application/xml");

                try (var response = httpClient.execute(get)) {

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

}
