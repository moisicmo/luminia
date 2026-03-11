package bo.com.luminia.sflbilling.msbatch.service;

import bo.com.luminia.sflbilling.msbatch.repository.CuisRepository;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msbatch.service.dto.RequestSync;
import bo.com.luminia.sflbilling.msbatch.service.dto.ResponseSync;
import bo.com.luminia.sflbilling.msbatch.service.sync.*;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.Cuis;
import bo.com.luminia.sflbilling.msbatch.service.sync.*;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.CuisNotFoundException;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.SyncParameterReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.SyncRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SyncParameterService {

    private final CuisRepository cuisRepository;

    private final ActivitySyncService activitySyncService;
    private final BroadcastTypeSyncService broadcastTypeSyncService;
    private final CancellationReasonSyncService cancellationReasonSyncService;
    private final OriginCountrySyncService originCountrySyncService;
    private final CurrencyTypeSyncService currencyTypeSyncService;
    private final DocumentSectorSyncService documentSectorSyncService;
    private final IdentityDocumentTypeSyncService identityDocumentTypeSyncService;
    private final InvoiceLegendSyncService invoiceLegendSyncService;
    private final InvoiceTypeSyncService invoiceTypeSyncService;
    private final MeasurementUnitSyncService measurementUnitSyncService;
    private final PaymentMethodTypeSyncService paymentMethodTypeSyncService;
    private final PointSaleTypeSyncService pointSaleTypeSyncService;
    private final ProductServiceSyncService productServiceSyncService;
    private final RoomTypeSyncService roomTypeSyncService;
    private final SectorDocumentTypeSyncService sectorDocumentTypeSyncService;
    private final ServiceMessageSyncService serviceMessageSyncService;
    private final SignificantEventSyncService significantEventSyncService;

    @Transactional
    public SyncRes synchronize(SyncParameterReq syncParameterReq, Company company) {
        // Obtiene el Cuis vigente del punto de venta.
        Optional<Cuis> cuis = cuisRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(syncParameterReq.getPointSaleSiat(), syncParameterReq.getBranchOfficeSiat(), syncParameterReq.getCompanyId());
        if (!cuis.isPresent()) {
            throw new CuisNotFoundException("No existe un CUIS vigente para el punto de venta");
            //return new SyncRes(false, "No existe un CUIS vigente para el punto de venta");
        }

        RequestSync requestSync = new RequestSync();
        requestSync.setCompanyId(company.getId().intValue());
        requestSync.setNit(company.getNit());
        requestSync.setSystemCode(company.getSystemCode());
        requestSync.setEnvironmentSiat(company.getEnvironmentSiat().getKey());
        requestSync.setPointSaleSiat(syncParameterReq.getPointSaleSiat());
        requestSync.setBranchOfficeSiat(syncParameterReq.getBranchOfficeSiat());
        requestSync.setCuis(cuis.get().getCuis());
        requestSync.setToken(company.getToken());

        ResponseSync response = activitySyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = broadcastTypeSyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = cancellationReasonSyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = originCountrySyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = currencyTypeSyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = documentSectorSyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = identityDocumentTypeSyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = invoiceLegendSyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = invoiceTypeSyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = measurementUnitSyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = paymentMethodTypeSyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = pointSaleTypeSyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = productServiceSyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = roomTypeSyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = sectorDocumentTypeSyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = serviceMessageSyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }
        response = significantEventSyncService.synchronize(requestSync);
        if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
            return new SyncRes(false, response.getMessage());
        }

        return new SyncRes(true, "La sincronización terminó correctamente.");
    }
}
