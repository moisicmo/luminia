package bo.gob.impuestos.sfe.invoice.xml;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class XmlBaseInvoice<H, D> implements XmlBaseInvoiceStrategy {

    protected String noNamespaceSchemaLocation;
    protected H cabecera;
    protected List<D> detalle;
    protected static Map<String, Source> listOfXsdFiles;

    public XmlBaseInvoice(String noNamespaceSchemaLocation) throws FileNotFoundException, URISyntaxException {
        this.noNamespaceSchemaLocation = noNamespaceSchemaLocation;
        init(noNamespaceSchemaLocation);
    }

    public XmlBaseInvoice() throws FileNotFoundException, URISyntaxException {
        init(noNamespaceSchemaLocation);
    }

    private void init(String noNamespaceSchemaLocation) throws FileNotFoundException, URISyntaxException {
        this.noNamespaceSchemaLocation = noNamespaceSchemaLocation;
        /*if (listOfXsdFiles == null) {
            listOfXsdFiles = new HashMap<>();
        }

        if (!listOfXsdFiles.containsKey(noNamespaceSchemaLocation)) {

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            InputStream stream = XmlBaseInvoice.class.getResourceAsStream(String.format("/invoice/%s", noNamespaceSchemaLocation));

            Source schemaSource = new StreamSource(stream);
            //URL url = ResourceUtils.getRes(String.format("classpath:invoice/%s", noNamespaceSchemaLocation));
            //File f = new File(url.toURI());
            listOfXsdFiles.put(noNamespaceSchemaLocation, schemaSource);
        }*/
    }

    /**
     * @return
     */
    protected Source readXsdFile() {
        /*if (listOfXsdFiles.isEmpty())
            return null;

        return listOfXsdFiles.get(this.noNamespaceSchemaLocation);\
        */

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        InputStream stream = XmlBaseInvoice.class.getResourceAsStream(String.format("/invoice/%s", noNamespaceSchemaLocation));

        Source schemaSource = new StreamSource(stream);

        return schemaSource ;


    }

    /**
     * Genera el contenido XML de acuerdo al invoice heredado.
     *
     * @param formatted Establece si la salida estara formateada para XML
     * @return Codigo XML generado de acuerdo a la clase base
     * @throws Exception
     */
    @Override
    public String generate(boolean formatted) throws Exception {
        JAXBContext context = JAXBContext.newInstance(getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formatted);
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, noNamespaceSchemaLocation);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
        //SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        StringWriter writer = new StringWriter();
        marshaller.marshal(this, writer);
        String xmlStr = writer.toString();

        return xmlStr;
    }

    @Override
    public boolean isValidXml(String xmlStr) throws XmlNotValidException {

        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = null;
            schema = schemaFactory.newSchema(readXsdFile());
            Validator validator = schema.newValidator();

            DocumentBuilderFactory domParser = DocumentBuilderFactory.newInstance();
            domParser.setValidating(false);
            domParser.setSchema(schema);
            domParser.setNamespaceAware(true);

            DocumentBuilder builder = domParser.newDocumentBuilder();
            Document result = builder.parse(new InputSource(new StringReader(xmlStr)));

            validator.validate(new DOMSource(result));

        } catch (SAXException e) {
            e.printStackTrace();
            log.error("Contenido XML no valido", e);
            throw new XmlNotValidException("Contenido XML No valido :"+e.getMessage());

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new XmlNotValidException("Contenido XML No valido :"+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new XmlNotValidException("Contenido XML No valido :"+e.getMessage());
        }


        return true;
    }

    public void setCabecera(H cabecera) {
        this.cabecera = cabecera;
    }

    public void setDetalle(List<D> detalle) {
        this.detalle = detalle;
    }

    @XmlTransient
    public abstract H getCabecera();

    @XmlTransient
    public abstract List<D> getDetalle();

    public abstract void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail);
}
