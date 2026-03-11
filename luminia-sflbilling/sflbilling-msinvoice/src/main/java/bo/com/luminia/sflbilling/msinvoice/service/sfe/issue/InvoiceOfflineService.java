package bo.com.luminia.sflbilling.msinvoice.service.sfe.issue;

import bo.com.luminia.sflbilling.msinvoice.repository.*;
import bo.com.luminia.sflbilling.domain.*;
import bo.com.luminia.sflbilling.msinvoice.repository.*;
import bo.com.luminia.sflbilling.msinvoice.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec.CufdNotFoundException;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceOfflineManualReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceOfflineReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.response.sfe.invoice.InvoiceOfflineRes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class InvoiceOfflineService {

    private final OfflineRepository offlineRepository;
    private final EventRepository eventRepository;
    private final SignificantEventRepository significantEventRepository;
    private final CompanyRepository companyRepository;
    private final BranchOfficeRepository branchOfficeRepository;
    private final PointSaleRepository pointSaleRepository;
    private final CufdRepository cufdRepository;

    public InvoiceOfflineRes get() {
        Optional<Offline> searchOffline = offlineRepository.findAll().stream().findFirst();
        if (!searchOffline.isPresent()) {
            return new InvoiceOfflineRes(SiatResponseCodes.ERROR_OFFLINE_NOT_FOUND, "No existe el registro Fuera de Linea.");
        }

        InvoiceOfflineRes response = new InvoiceOfflineRes();
        response.setCode(SiatResponseCodes.SUCCESS);
        response.setBody(searchOffline.get());
        return response;
    }

    public InvoiceOfflineRes offline(InvoiceOfflineReq invoiceOfflineReq) {
        if (invoiceOfflineReq.getSignificantEventSiatId() < 1 || invoiceOfflineReq.getSignificantEventSiatId() > 4) {
            return new InvoiceOfflineRes(SiatResponseCodes.ERROR_OFFLINE_EVENT_NOT_VALID, "Valor permitido para el evento significativo entre 1-4.");
        }
        Optional<Offline> searchOffline = offlineRepository.findAll().stream().findFirst();
        if (!searchOffline.isPresent()) {
            return new InvoiceOfflineRes(SiatResponseCodes.ERROR_OFFLINE_NOT_FOUND, "No existe el registro Fuera de Linea.");
        }
        Offline updateOffline = searchOffline.get();
        // Verifica si el sistema ya se encuentra fuera de linea.
        if (updateOffline.getActive()) {
            return new InvoiceOfflineRes(SiatResponseCodes.ERROR_OFFLINE_ACTIVATE, "El sistema ya se encuentra Fuera de Linea.");
        }
        updateOffline.setActive(true);
        Offline offline = offlineRepository.save(updateOffline);
        log.debug("Fuera de linea activo: {}", offline);

        // Itera la lista de empresas habilitados para fuera de linea.
        List<Company> listCompany = companyRepository.findAllByEventSendIsTrue();
        for (Company company : listCompany) {
            // Itera la lista de sucursales.
            List<BranchOffice> listBranchOffice = branchOfficeRepository.findAllByCompanyIdAndActiveIsTrue(company.getId());
            for (BranchOffice branchOffice : listBranchOffice) {
                // Itera la lista de puntos de venta.
                List<PointSale> listPointSale = pointSaleRepository.findAllByBranchOfficeIdAndActiveIsTrue(branchOffice.getId());
                for (PointSale pointSale : listPointSale) {

                    // 4. Obtiene el CUFD activo.
                    Optional<Cufd> cufdOptional = null;
                    try {
                        cufdOptional = this.obtainCufdActive(company.getId(), branchOffice.getBranchOfficeSiatId(), pointSale.getPointSaleSiatId());
                    } catch (CufdNotFoundException e) {
                        throw new CufdNotFoundException();
                        //return new InvoiceOfflineRes(SiatResponseCodes.ERROR_CUFD_NOT_FOUND, "Código Cufd no encontrado o no ha sido generado");
                    }
                    Cufd cufdFromDb = cufdOptional.get();
                    log.debug("4. Datos del código CUFD : {}", cufdFromDb);

                    Event newEvent = new Event();
                    newEvent.setCufdEvent(cufdFromDb.getCufd());
                    newEvent.setStartDate(ZonedDateTime.now());
                    newEvent.setEndDate(ZonedDateTime.now());
                    newEvent.setReceptionCode(null);
                    newEvent.setDescription(invoiceOfflineReq.getDescription());
                    newEvent.setActive(true);
                    newEvent.setCafc(null);
                    newEvent.setBranchOffice(branchOffice);
                    newEvent.setPointSale(pointSale);
                    newEvent.setSignificantEvent(significantEventRepository.findByCompanyIdAndSiatIdAndActiveTrue(company.getId().intValue(), invoiceOfflineReq.getSignificantEventSiatId()));

                    eventRepository.save(newEvent);
                    log.debug("Registro evento preliminar: {}", newEvent);
                }
            }
        }
        return new InvoiceOfflineRes(SiatResponseCodes.SUCCESS, "Sistema Fuera de Linea.");
    }

    public InvoiceOfflineRes offlineManual(InvoiceOfflineManualReq invoiceOfflineManualReq) {
        if (invoiceOfflineManualReq.getSignificantEventSiatId() < 5 || invoiceOfflineManualReq.getSignificantEventSiatId() > 7) {
            return new InvoiceOfflineRes(SiatResponseCodes.ERROR_OFFLINE_EVENT_NOT_VALID, "Valor permitido para el evento significativo entre 5-7.");
        }
        Optional<Company> companyFromDb = companyRepository.findCompanyByIdAndActiveIsTrue(invoiceOfflineManualReq.getCompanyId());
        Company company = null;
        if (companyFromDb.isPresent()) {
            company = companyFromDb.get();
            log.debug("Recupera datos de la empresa : {}", company);
        }

        // Itera la lista de sucursales.
        List<BranchOffice> listBranchOffice = branchOfficeRepository.findAllByCompanyIdAndActiveIsTrue(company.getId());
        for (BranchOffice branchOffice : listBranchOffice) {
            // Itera la lista de puntos de venta.
            List<PointSale> listPointSale = pointSaleRepository.findAllByBranchOfficeIdAndActiveIsTrue(branchOffice.getId());
            for (PointSale pointSale : listPointSale) {

                // Obtiene el CUFD activo.
                Optional<Cufd> cufdOptional = null;
                try {
                    cufdOptional = this.obtainCufdActive(company.getId(), branchOffice.getBranchOfficeSiatId(), pointSale.getPointSaleSiatId());
                } catch (CufdNotFoundException e) {
                    throw new CufdNotFoundException();
                    //return new InvoiceOfflineRes(SiatResponseCodes.ERROR_CUFD_NOT_FOUND, "Código Cufd no encontrado o no ha sido generado");
                }
                Cufd cufdFromDb = cufdOptional.get();
                log.debug("Datos del código CUFD : {}", cufdFromDb);

                Event newEvent = new Event();
                newEvent.setCufdEvent(cufdFromDb.getCufd());
                newEvent.setStartDate(ZonedDateTime.now());
                newEvent.setEndDate(ZonedDateTime.now());
                newEvent.setReceptionCode(null);
                newEvent.setDescription(invoiceOfflineManualReq.getDescription());
                newEvent.setActive(true);
                newEvent.setCafc(invoiceOfflineManualReq.getCafc());
                newEvent.setBranchOffice(branchOffice);
                newEvent.setPointSale(pointSale);
                newEvent.setSignificantEvent(significantEventRepository.findByCompanyIdAndSiatIdAndActiveTrue(company.getId().intValue(), invoiceOfflineManualReq.getSignificantEventSiatId()));

                eventRepository.save(newEvent);
                log.debug("Registro evento preliminar: {}", newEvent);
            }
        }
        return new InvoiceOfflineRes(SiatResponseCodes.SUCCESS, "Emisión manual habilitada correctamente.");
    }

    public InvoiceOfflineRes online() {
        Optional<Offline> searchOffline = offlineRepository.findAll().stream().findFirst();
        if (!searchOffline.isPresent()) {
            return new InvoiceOfflineRes(SiatResponseCodes.ERROR_OFFLINE_NOT_FOUND, "No existe el registro Fuera de Linea.");
        }
        Offline updateOffline = searchOffline.get();
        // Verifica si el sistema ya se encuentra en linea.
        if (updateOffline.getActive().equals(false)) {
            return new InvoiceOfflineRes(SiatResponseCodes.ERROR_OFFLINE_ACTIVATE, "El sistema ya se encuentra En Linea.");
        }
        updateOffline.setActive(false);
        Offline offline = offlineRepository.save(updateOffline);
        log.debug("En linea activo: {}", offline);

        return new InvoiceOfflineRes(SiatResponseCodes.SUCCESS, "Sistema En Linea.");
    }

    /**
     * Método que obtiene el código CUFD vigente.
     *
     * @param companyId        Id de la empresa.
     * @param branchOfficeSiat Id de la sucursal Siat.
     * @param pointSaleSiat    Id punto de venta Siat.
     * @return
     * @throws CufdNotFoundException
     */
    protected Optional<Cufd> obtainCufdActive(Long companyId, int branchOfficeSiat, int pointSaleSiat) throws CufdNotFoundException {
        Optional<Cufd> cufdOptional = cufdRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(pointSaleSiat, branchOfficeSiat, companyId);
        if (!cufdOptional.isPresent()) {
            throw new CufdNotFoundException();
        }
        return cufdOptional;
    }
}
