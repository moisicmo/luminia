package bo.com.luminia.sflbilling.msinvoice.service.utils;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;

public class FileHash {

    /**
     * Método que hashea un archivo en SHA256.
     *
     * @param file Archivo.
     * @return
     */
    public static String sha256(byte[] file) {
        String hashValue = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(file);
            byte[] digestedBytes = messageDigest.digest();
            hashValue = DatatypeConverter.printHexBinary(digestedBytes).toLowerCase();
        } catch (Exception e) {
            System.out.println("Error en la generación del Hash");
        }
        return hashValue;
    }
}
