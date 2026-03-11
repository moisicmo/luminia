package bo.com.luminia.sflbilling.msreport.web.rest.response.data;

import java.util.List;

public class CompraVentaInvoice {
    private CompraVentaHeader cabecera;
    private List<CompraVentaDetail> detalle;

    public CompraVentaHeader getCabecera() {
        return cabecera;
    }

    public void setCabecera(CompraVentaHeader cabecera) {
        this.cabecera = cabecera;
    }

    public List<CompraVentaDetail> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<CompraVentaDetail> detalle) {
        this.detalle = detalle;
    }
}
