package bo.com.luminia.sflbilling.msreport.web.rest.response;

import lombok.Data;

@Data
public class WrapperEventRes {
    private Long id;
    private String cufDEvent;
    private String startDate;
    private String endDate;
    private String receptionCode;
    private String status;
    private String companyName;
    private Long companyId;
    private String branchOfficeName;
    private Long branchOfficeId;
    private String pointSaleName;
    private Long pointSaleId;
    private String significantEventName;
    private String significantEventDescription;
    private Long significantEventId;
    private String sectorDocumentTypeName;
    private Long sectorDocumentTypeId;
}
