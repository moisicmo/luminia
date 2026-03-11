package bo.gob.impuestos.sfe.invoice.xml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XmlDateHandler extends StdDeserializer<Date> {

    public XmlDateHandler(){
        this(null);
    }

    public XmlDateHandler(Class<?> vc) {
        super(vc);
    }

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String date = jsonParser.getText();
        String dateFormat= "dd-MM-yyyy HH:mm:ss" ;

        if (date.contains("T")){
            dateFormat = "dd-MM-yyyy'T'HH:mm:ss" ;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null ;
    }
}
