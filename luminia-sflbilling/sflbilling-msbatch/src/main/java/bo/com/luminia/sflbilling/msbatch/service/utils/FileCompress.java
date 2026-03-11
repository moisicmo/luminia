package bo.com.luminia.sflbilling.msbatch.service.utils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class FileCompress {

    /**
     * Método de empaquetado un texto a formato GZIP.
     *
     * @param str Texto.
     * @return
     * @throws Exception
     */
    public static byte[] textToGzip(String str) throws Exception {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(stream);
        gzip.write(str.getBytes(StandardCharsets.UTF_8));
        gzip.close();
        return stream.toByteArray();
    }

    /**
     * Método de empaquetado un archivo a formato GZIP.
     *
     * @param file Archivo.
     * @return
     * @throws Exception
     */
    public static byte[] fileToGzip(byte[] file) throws Exception {
        if (file == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(stream);
        gzip.write(file);
        gzip.close();
        return stream.toByteArray();
    }
}
