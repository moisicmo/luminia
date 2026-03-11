package bo.com.luminia.sflbilling.msreport.siat.pdf;

import com.google.zxing.WriterException;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface PdfProxy<T> {

    public byte[] generate() throws IOException, WriterException, JRException, URISyntaxException;

}
