package br.com.jbssistemas.efdclient.service;

import br.com.jbssistemas.efdclient.model.CertData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;

import static br.com.jbssistemas.efdclient.util.XmlUtils.parseXmlStringToDocument;

@Service
@RequiredArgsConstructor
public class SignerReinfDocumentService {

    private static final String SIGNATURE_METHOD = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";

    private final KeyStoreService keyStoreService;

    public Document sign(Document doc, CertData cert) {

        try {
            var keyEntry = keyStoreService.getKeyEntry(cert);
            var rootItem = doc.getDocumentElement();

            String elementoID = null;
            Element childNode = null;

            var childNodes = rootItem.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                if (Element.class.isInstance(childNodes.item(i))) {
                    childNode = Element.class.cast(childNodes.item(i));
                    elementoID = childNode.getAttribute("id");
                    if (elementoID != null) {
                        break;
                    }
                }
            }

            if (elementoID == null || elementoID.isEmpty()) {
                throw new NoSuchFieldException("Campo ID não encontrado na tag do Evento!");
            }

            childNode.setIdAttribute("id", true);

            if (keyEntry == null) {
                throw new InvalidKeyException("ID incorreto do certificado digital!");
            }

            var dsc = new DOMSignContext(keyEntry.getPrivateKey(), rootItem);

            var fac = XMLSignatureFactory.getInstance("DOM");

            var transforms = new ArrayList<Transform>();
            transforms.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
            transforms.add(fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null));

            var ref = fac.newReference("#" + elementoID,
                    fac.newDigestMethod(DigestMethod.SHA256, null),
                    transforms, null, null);

            var si = fac.newSignedInfo(
                    fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                            (C14NMethodParameterSpec) null),
                    fac.newSignatureMethod(SIGNATURE_METHOD, null),
                    Collections.singletonList(ref));

            var kif = fac.getKeyInfoFactory();

            var x509Content = new ArrayList<Certificate>();
            x509Content.add(keyEntry.getCertificate());

            var kv = kif.newX509Data(x509Content);
            var ki = kif.newKeyInfo(Collections.singletonList(kv));
            var signature = fac.newXMLSignature(si, ki);

            signature.sign(dsc);

            var tf = TransformerFactory.newInstance();
            var trans = tf.newTransformer();

            var output = new ByteArrayOutputStream();
            trans.transform(new DOMSource(doc), new StreamResult(output));

            return parseXmlStringToDocument(output.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
