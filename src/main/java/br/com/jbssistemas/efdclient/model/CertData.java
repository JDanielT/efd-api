package br.com.jbssistemas.efdclient.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CertData {

    private String alias;
    private String keystorePath;
    private String keystorePassword;

}
