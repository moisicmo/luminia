package bo.gob.impuestos.sfe.handler;


import lombok.extern.slf4j.Slf4j;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

@Slf4j
public class JaxWsLoggingHandler implements SOAPHandler<SOAPMessageContext> {

    @Override
    public void close(MessageContext arg0) {
    }

    @Override
    public boolean handleFault(SOAPMessageContext arg0) {
        SOAPMessage message = arg0.getMessage();
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            message.writeTo(byteArrayOutputStream);
            log.info("Fault: {}", byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();

        } catch (SOAPException | IOException e) {
            log.error("Failed to handle fault: {}", e.getMessage());
        }
        return true;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext arg0) {
        SOAPMessage message = arg0.getMessage();
        boolean isOutboundMessage = (Boolean) arg0.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        String msg;
        if (isOutboundMessage) {
            msg = "Soap Request: {}";

        } else {
            msg = "Soap Response: {}";
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            message.writeTo(byteArrayOutputStream);
            log.info(msg, byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();

        } catch (SOAPException | IOException e) {
            log.error("Failed to handle message: {}", e.getMessage());
        }
        return true;
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

}
