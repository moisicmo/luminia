package bo.com.luminia.sflbilling.msreport.siat.pdf;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfQrGenerator {

    public static byte[] generate(String nit, String cuf, String numero, String tipo, String url ) throws WriterException, IOException {
        String value = String.format(url, nit, cuf, numero, tipo);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(value, BarcodeFormat.QR_CODE, 87, 86);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        return pngData;
    }

    public static Image generateImage(String nit, String cuf, String numero, String tipo, String url) throws WriterException, IOException {
        String value = String.format(url, nit, cuf, numero, tipo);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(value, BarcodeFormat.QR_CODE, 120, 121);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pngData);
        BufferedImage image = ImageIO.read(inputStream);
        return image;
    }
}
