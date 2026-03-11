package bo.gob.impuestos.sfe.invoice.xml;



import org.xml.sax.SAXException;

import java.io.IOException;

public interface XmlBaseInvoiceStrategy {

    String generate(boolean formated)  throws Exception;

    boolean isValidXml(String xmlStr) throws SAXException, IOException, XmlNotValidException;

}
