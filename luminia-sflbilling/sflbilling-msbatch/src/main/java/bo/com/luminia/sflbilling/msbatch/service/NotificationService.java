package bo.com.luminia.sflbilling.msbatch.service;

import bo.com.luminia.sflbilling.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void notifyCufCuisNotSync(Company company) {
        //Esto debe ser temporal, se debe sincronizar de otra manera
        StringBuilder builder = new StringBuilder();
        builder.append("********************************************\n");
        builder.append("URGENTE: Compañía no sincronizó cuis/cuf \n");
        builder.append("En el paso a offline \n");
        builder.append("Compañía id=").append(company.getNit()).append(" \n");
        builder.append("Compañía name=").append(company.getName()).append(" \n");
        builder.append("Compañía business name=").append(company.getBusinessName()).append(" \n");
        builder.append("********************************************\n");
        log.error(builder.toString());
    }

    public void notifyEventPackError(Company company, BranchOffice branchOffice, PointSale pointSale, Event event) {
        //Esto debe ser temporal, se debe sincronizar de otra manera
        StringBuilder builder = new StringBuilder();
        builder.append("********************************************\n");
        builder.append("URGENTE: Evento no sincronizado por cuis/cuf no activos o no generados\n");
        builder.append("Compañía id=").append(company.getNit()).append(" \n");
        builder.append("Compañía name=").append(company.getName()).append(" \n");
        builder.append("Compañía business name=").append(company.getBusinessName()).append(" \n");
        builder.append("Sucursal id=").append(branchOffice.getId()).append(" \n");
        builder.append("PointSale id=").append(pointSale.getId()).append(" \n");
        builder.append("********************************************\n");
        log.error(builder.toString());
    }

    public void notifyMassiveInvoiceError(Company company, String descripcion) {
        //De momento nada, ya se imprime el error en el log
    }

    public void notifyEventNotSyncError(Event event, String message) {
        StringBuilder builder = new StringBuilder();
        builder.append("********************************************\n");
        builder.append("URGENTE:Evento no sincronizado con impuestos\n");
        builder.append("Event id=").append(event.getId()).append("\n");
        builder.append("Error msg=").append(message).append("\n");
        log.error(builder.toString());
    }


    public void notifyFailToCheckRequestToRevert(InvoiceRequest invoiceRequest) {
        StringBuilder builder = new StringBuilder();
        builder.append("********************************************\n");
        builder.append("URGENTE: Falla en revisar request fallido, posible factura generada \n");
        builder.append("id=").append(invoiceRequest.getId()).append("\n");
        log.error(builder.toString());
    }
}
