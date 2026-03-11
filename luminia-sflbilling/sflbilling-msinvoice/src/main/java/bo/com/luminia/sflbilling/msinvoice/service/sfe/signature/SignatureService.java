package bo.com.luminia.sflbilling.msinvoice.service.sfe.signature;

import bo.com.luminia.sflbilling.domain.Signature;
import bo.com.luminia.sflbilling.msinvoice.repository.SignatureRepository;
import bo.com.luminia.sflbilling.msinvoice.service.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec.DefaultTransactionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Optional;

@Slf4j
@Service
public class SignatureService {

    @Autowired
    private SignatureRepository signatureRepository;

    /**
     * Constructor de la clase que inicia el servicio del firmador.
     */
    public SignatureService() {
        Init.init();
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Método firmador del documento Xml.
     *
     * @param xml       Xml en texto.
     * @param companyId Id de la empresa.
     * @return
     */
    public String singXml(String xml, Long companyId) throws DefaultTransactionException {
        byte[] data = xml.getBytes(StandardCharsets.UTF_8);
        try {
            // Obtiene la llave privada y el certificado de la empresa.
            Optional<Signature> obj = signatureRepository.findByCompanyActive(companyId);
            if (obj.isPresent()) {
                // Generación de la llave privada y certificado.
                PrivateKey privateKey = this.getPrivateKey(obj.get().getPrivateKey());
                X509Certificate cert = this.getX509Certificate(obj.get().getCertificate());
                // Firmado del documento XML.
                byte[] xmlSign = this.sign(data, privateKey, cert);
                return new String(xmlSign, StandardCharsets.UTF_8);
            }
            return "";
        } catch (IOException | GeneralSecurityException ex) {
            ex.printStackTrace();
            throw new DefaultTransactionException(ResponseMessages.ERROR_GENERAR_FIRMA,"Signature", "SIGNATURE_EXCEPTION");
            //return ex.getMessage();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new DefaultTransactionException(ResponseMessages.ERROR_GENERAR_FIRMA,"Signature", "SIGNATURE_EXCEPTION");
            //return e.getMessage();
        } catch (XMLSecurityException e) {
            e.printStackTrace();
            throw new DefaultTransactionException(ResponseMessages.ERROR_GENERAR_FIRMA,"Signature", "SIGNATURE_EXCEPTION");
            //return e.getMessage();
        } catch (SAXException e) {
            e.printStackTrace();
            throw new DefaultTransactionException(ResponseMessages.ERROR_GENERAR_FIRMA,"Signature", "SIGNATURE_EXCEPTION");
            //return e.getMessage();
        }
    }

    /**
     * Método que genera la llave privada.
     *
     * @param key Llava privada en texto.
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private RSAPrivateKey getPrivateKey(String key) throws IOException, GeneralSecurityException {
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        //TODO DELETE ESTE
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\r\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        byte[] encoded = Base64.decodeBase64(privateKeyPEM);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        RSAPrivateKey privateKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * Método para la generación del certificado.
     *
     * @param cert Certificado en texto.
     * @return
     * @throws IOException
     * @throws CertificateException
     */
    private X509Certificate getX509Certificate(String cert) throws IOException, CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        InputStream stream = new ByteArrayInputStream(cert.getBytes());
        X509Certificate certificate = (X509Certificate) fact.generateCertificate(stream);
        return certificate;
    }

    /**
     * Método para realizar la firma del documento Xml.
     *
     * @param data        Datos.
     * @param privateKey  Llave privada.
     * @param certificate Certificado.
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XMLSecurityException
     */
    private byte[] sign(byte[] data, PrivateKey privateKey, X509Certificate... certificate) throws ParserConfigurationException, IOException, SAXException, XMLSecurityException {
        ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "");
        Document document = readXml(data);
        Element root = (Element) document.getFirstChild();
        document.setXmlStandalone(false);
        XMLSignature signature = new XMLSignature(document, null, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256);
        root.appendChild(signature.getElement());
        Transforms transforms = new Transforms(document);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
        signature.addDocument("", transforms, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
        if (certificate != null) {
            signature.addKeyInfo(certificate[0]);
        }
        signature.sign(privateKey);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLUtils.outputDOMc14nWithComments(document, baos);
        return baos.toByteArray();
    }

    /**
     * Método para leer el documento Xml.
     *
     * @param data Datos.
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private Document readXml(byte[] data) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        factory.setNamespaceAware(true);
        builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(data));
    }
}
