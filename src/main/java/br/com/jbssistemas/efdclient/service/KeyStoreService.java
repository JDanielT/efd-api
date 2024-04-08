package br.com.jbssistemas.efdclient.service;

import br.com.jbssistemas.efdclient.model.CertData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.KeyStoreException;

@Service
@RequiredArgsConstructor
public class KeyStoreService {

    public KeyStore.PrivateKeyEntry getKeyEntry(CertData cert) throws Exception {

        var alias = cert.getAlias();
        var pass = cert.getKeystorePassword().toCharArray();

        var ks = this.getKeyStore(cert);

        try {
            return (KeyStore.PrivateKeyEntry) ks.getEntry(alias, new KeyStore.PasswordProtection(pass));
        } catch (Exception e) {
            throw new Exception("Falha no Certificado Digital!", e);
        }

    }

    public KeyStore getKeyStore(CertData cert) throws KeyStoreException, FileNotFoundException {

        var alias = cert.getAlias();
        var pass = cert.getKeystorePassword().toCharArray();

        var ks = KeyStore.getInstance("PKCS12");

        var fileInputStream = new FileInputStream(cert.getKeystorePath());

        try {
            ks.load(fileInputStream, pass);
            if (ks.getEntry(alias, new KeyStore.PasswordProtection(pass)) == null) {
                throw new IllegalArgumentException("ID do Certificado Digital incorreto!");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Senha do Certificado Digital incorreta ou Certificado invalido!", e);
        }

        return ks;

    }

}
