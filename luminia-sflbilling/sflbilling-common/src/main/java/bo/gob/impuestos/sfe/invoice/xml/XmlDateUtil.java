package bo.gob.impuestos.sfe.invoice.xml;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XmlDateUtil {

    /**
     *   Date format input should be
     *      "dd-MM-yyyy'T'HH:mm:ss.SSS"
     * @param date fecha entrada
     * @return
     */
    public static Date parseDateToYyyymmddThhmmssSSS(String date){
        SimpleDateFormat formatter6=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            Date result =formatter6.parse(date);
            return result ;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null ;
    }

    /**
     * Convierte de Tipo de dato Date a String dd/MM/yyyy hh:m aa
     * @param date
     * @return
     */
    public static String convertDateToYyyymmddhhmmssSSS(Date date) {
        String pattern = "dd/MM/yyyy hh:mm aa";
        DateFormat df = new SimpleDateFormat(pattern);
        String result = df.format(date) ;

        return result ;
    }
}
