package bo.com.luminia.sflbilling.msinvoice.invoice;

import bo.com.luminia.sflbilling.msinvoice.repository.*;
import bo.com.luminia.sflbilling.domain.*;
import bo.com.luminia.sflbilling.domain.enumeration.EnvironmentSiatEnum;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import bo.com.luminia.sflbilling.msinvoice.repository.*;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec.XmlFailedConversionException;
import bo.com.luminia.sflbilling.msinvoice.service.sfe.issue.InvoiceIssueService;
import bo.com.luminia.sflbilling.msinvoice.siat.invoice.xml.XmlInvoiceAdapter;
import bo.com.luminia.sflbilling.msinvoice.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceIssueReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.sync.SyncParameterReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.response.sfe.invoice.InvoiceIssueRes;
import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlSectorType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class XmlInvoiceControllerITest {

    @Autowired
    private CompanyRepository companyRepository ;
    @Autowired
    private BranchOfficeRepository branchOfficeRepository;
    @Autowired
    private PointSaleRepository pointSaleRepository ;
    @Autowired
    private SignatureRepository signatureRepository;
    @Autowired
    private ApprovedProductRepository approvedProductRepository ;
    @Autowired
    private ProductServiceRepository productServiceRepository ;
    @Autowired
    private MeasurementUnitRepository unitRepository;
    @Autowired
    private InvoiceLegendRepository legendRepository ;

    //@Autowired
    //private SyncCodeService syncCodeService;
    //@Autowired
    //private SyncParameterService syncParameterService;
    @Autowired
    private  InvoiceIssueService issueService;

    private Company company ;
    private BranchOffice branchOffice;
    private Signature signature;

    private void createCompany(){
        company = new Company();
        company.setNit(1028675024L);
        company.setName("LUMINIA");
        company.setBusinessName("LUMINIA");
        company.setCity("BOLIVIA");
        company.setPhone("78859964");
        company.setAddress("Calle 20 de Octubre");
        company.setSystemCode("6CE0F4E55826C2DB97038EF");
        company.setPackageSend(false);
        company.setEventSend(true);
        company.setEnvironmentSiat(EnvironmentSiatEnum.TEST);
        company.setModalitySiat(ModalitySiatEnum.ELECTRONIC_BILLING);

        company = companyRepository.saveAndFlush(company);
    }

    private void createBranchOffice(){
        branchOffice = new BranchOffice();
        branchOffice.setBranchOfficeSiatId(0);
        branchOffice.setName("SUCURSAL MATRIZ");
        branchOffice.setDescription("LUMINIA SUCURSAL MATRIZ");
        branchOffice.setActive(true);
        branchOffice.setCompany(company);

        branchOffice = branchOfficeRepository.saveAndFlush(branchOffice);
    }

    private void createPoinOfSale(){
        PointSale ps1 = new PointSale();
        ps1.setPointSaleSiatId(0);
        ps1.setName("PUNTO VENTA 0");
        ps1.setDescription("PUNTO VENTA 0");
        ps1.setActive(true);
        ps1.setBranchOffice(branchOffice);
        ps1=pointSaleRepository.saveAndFlush(ps1);

        PointSale ps2 = new PointSale();
        ps2.setPointSaleSiatId(1);
        ps2.setName("PUNTO VENTA 1");
        ps2.setDescription("PUNTO VENTA 1");
        ps2.setActive(true);
        ps2.setBranchOffice(branchOffice);
        ps2=pointSaleRepository.saveAndFlush(ps2);

        PointSale ps3 = new PointSale();
        ps3.setPointSaleSiatId(2);
        ps3.setName("PUNTO VENTA 2");
        ps3.setDescription("PUNTO VENTA 2");
        ps3.setActive(true);
        ps3.setBranchOffice(branchOffice);
        ps3=pointSaleRepository.saveAndFlush(ps3);
    }

    private void createSignature(){
        Signature signature = new Signature();
        signature.setId(1L);
        signature.setCertificate("-----BEGIN CERTIFICATE-----\n" +
            "MIIEvjCCA6agAwIBAgIIgtMB5t5OwAAwDQYJKoZIhvcNAQEFBQAwgbUxCzAJBgNV\n" +
            "BAYTAkJPMQ8wDQYDVQQIDAZMQSBQQVoxDzANBgNVBAcMBkxBIFBBWjEeMBwGA1UE\n" +
            "CgwVRW50aWRhZCBDZXJ0aWZpY2Fkb3JhMQwwCgYDVQQLDANVSUQxEzARBgNVBAMM\n" +
            "CkFEU0lCIEZBS0UxJDAiBgkqhkiG9w0BCQEWFW5jb2FyaXRlQGFkc2liLmdvYi5i\n" +
            "bzEbMBkGA1UEBRMSNzM1MjQyNDI0NDY0NjM0MjM0MB4XDTIxMDcxNTE0NTgwMloX\n" +
            "DTIxMDgxNDE0NTgwMlowge4xJDAiBgNVBAMTG0VEVUFSRE8gUEVEUk8gQVJBTkRB\n" +
            "IE1PSklDQTEUMBIGA1UEChMLU0lOVEVTSVMgU0ExETAPBgNVBAsTCFNJTlRFU0lT\n" +
            "MRwwGgYDVQQMExNSRVBSRVNFTlRBTlRFIExFR0FMMQswCQYDVQQGEwJCTzELMAkG\n" +
            "A1UELhMCQ0kxFDASBgcrBgEBAQEAEwczMzY5NzM3MREwDwYKCZImiZPyLGQBARMB\n" +
            "MDETMBEGA1UEBRMKMTAyODY3NTAyNDEnMCUGCSqGSIb3DQEJARYYZWR1YXJkb2FA\n" +
            "c2ludGVzaXMuY29tLmJvMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA\n" +
            "vLOP2/W8Ao/lm25JJYh6uQVL7Ic34rS5c2DbLHBH6g4vStAMehDzCPSywzr98f3J\n" +
            "DO30T+4EjZR7DWLXLRQPSOJrTXw7gC/EI1rPFn6RWVLG2uRfeT6uUL6vAt23nHPn\n" +
            "uEeScfvKZbMxD5YcPMhcr6Z/cw67eGWWgIg9EDBqgG+xSnvW7wdeSisEv94ELsQs\n" +
            "ffXknY/OWUD2vsGir42dw7YLVivZ/LWsY0rryLbnBSD0x2A3ffGxJcfXRZV5EYQS\n" +
            "3SjQrDEbLddKX/77giflnUkfwp4EX/srhlS2UWxub8PYe3ZrF/vGppw+5n6mIjtg\n" +
            "yApa7BoOonqZdEg7JKbSiwIDAQABo4GWMIGTMAwGA1UdEwQFMAMBAf8wCwYDVR0P\n" +
            "BAQDAgTwMCMGA1UdEQQcMBqGGGVkdWFyZG9hQHNpbnRlc2lzLmNvbS5ibzBRBgNV\n" +
            "HR8ESjBIMEagRKBChkBodHRwczovL2Rlc2Fycm9sbG8uYWRzaWIuZ29iLmJvL2Rl\n" +
            "c2FfYWdlbmNpYS9saXN0X3Jldm9jYWNpb24uY3JsMA0GCSqGSIb3DQEBBQUAA4IB\n" +
            "AQC0jCxvku0oxSU5KTR06s091OVrt209rr9O1K1lkH7qkgl1zbeXJQzpdZEpwCGJ\n" +
            "TbNB1MvC5lZCTrattkkyinmzSmo+RdHccxDC6OIbsYBXsQtPCRnsnWQ+ACKdlG+m\n" +
            "FTH5nafNmjYYNX2x2aLekBzuB7diUhZ3Xzi/VW7TjzppLFWSDCxUmfvXg5TewNn1\n" +
            "omJ90V+mjYHv5ZZI1Z3+kpwV4qWZkVycxHQQRuswEcUY/aq6DpnxpEB1jKp1Zq9x\n" +
            "bMc8z8QonOe9sH2mJuadeGcMX+kdq8mj1nKfy2ubno90tF/6upqfQSRcLvJKAsOq\n" +
            "hwrZMxuEhU3qcwohv+nhj5js\n" +
            "-----END CERTIFICATE-----\n");
        signature.setPrivateKey("-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC8s4/b9bwCj+Wb\n" +
            "bkkliHq5BUvshzfitLlzYNsscEfqDi9K0Ax6EPMI9LLDOv3x/ckM7fRP7gSNlHsN\n" +
            "YtctFA9I4mtNfDuAL8QjWs8WfpFZUsba5F95Pq5Qvq8C3becc+e4R5Jx+8plszEP\n" +
            "lhw8yFyvpn9zDrt4ZZaAiD0QMGqAb7FKe9bvB15KKwS/3gQuxCx99eSdj85ZQPa+\n" +
            "waKvjZ3DtgtWK9n8taxjSuvItucFIPTHYDd98bElx9dFlXkRhBLdKNCsMRst10pf\n" +
            "/vuCJ+WdSR/CngRf+yuGVLZRbG5vw9h7dmsX+8amnD7mfqYiO2DIClrsGg6iepl0\n" +
            "SDskptKLAgMBAAECggEAQiKIo+W/DALUzSHc/wuTP29MycwmWQ1QBdEHX87GVZC9\n" +
            "IHJEPS/djeD9GhF92SdCJIbtXmokLF7CtdFTatgh90BrrQ+CZw3WpzF3bQHU/UJW\n" +
            "sXbiw0Klo2UBP3bnmCzDJMHo4IwHxpcD9dt9cAwk9+bZukCKkcrA8bFN/XOKkBJ6\n" +
            "8Gtf5J2FjkixZGbLcV5qmh8RcgqMz0OjqugL9pSfgMMEiuc9yqB1Ak2xGUF78DmP\n" +
            "UctG9HAQ8hLUhupU8UR3aoOj7WtMDpX10CIPvIUsh7+tP68LfilgfZR8YINvltB3\n" +
            "8qk86wRRJ8cwbwibKlz1L8RD7vAv4D+lCTIRMyuYeQKBgQDpXbamPP4yytXgkDDZ\n" +
            "V5vqH5NrCm7Dj64E9EyASkyik/TixwCJyCqR6YDufk0rNem6+Vqy8D99WoxPnK3w\n" +
            "FqC5t50/0tvUnoEtMlddmihMScIQmngPfFG1wBvZT9ycULFbQYDpa22IPsGoykNc\n" +
            "BpuWASgKlmqg23AWEpmLB7HIzwKBgQDPANyubW5QInodTBs92lAgewKHSybZv9x+\n" +
            "fwRjbBoMl2mhOIm3/kMhT4GpfRLi1TqNXpUm71GlG7Gv9u9zLxmYwQYPhDOcbQIY\n" +
            "VxV07dbzbVDb32BVpAXZP3wmHsxlK/eFEkSZcch0XvS4WL1mOm3Jll+8qpFWkVvW\n" +
            "o9GAf0BRhQKBgFjmUFxHw/aJeqyPgWxqiYTI/pm6YbOyGnLctf/xTfxpLNLvSG80\n" +
            "h7MKJwmzp6YcZavKrhiYmTchtW3mnARoOlZFcmwL4Z6/uyoCkXGg9lUJjBpTHgWY\n" +
            "MHByfKluWPZbbxT6gSdqu1E5xwCL/Nkj00Vzr1NJNdmNfseJ0mA6UCnVAoGAB3N5\n" +
            "fhHUUbAcAyf1JxHPpoum+KW83UOptSfvSYDfoypkE/iMBIJzeiR5f1dQMbgJJoOM\n" +
            "DN26a54GlFXoIpZEbposFKzmiq/lzmh8Djxta0+5BGES/6Iqz7oYRur+4nllrHWO\n" +
            "4JMW6xFr76LKFn7t6r6t7YWaO6p5ys0UwnJSJ10CgYEAh7uRi0I571KtI+PR6Ein\n" +
            "ULashkmXBIxv9cJcoQGMzzPjrwJYXkelbYOoyqYU+0yRouR6AnwCLO8UAzWfCr7j\n" +
            "wRnP9ZNfr5QKazUZklRsvIj8KLu0x+ugn3CpuyvDaA9Ij4JCVHTFw4kzAkebE/hT\n" +
            "aKRfChC+HQMStvzHScUZ6yk=\n" +
            "-----END PRIVATE KEY-----");
        signature.setStartDate(ZonedDateTime.now(ZoneId.of("UTC")));
        signature.setEndDate(ZonedDateTime.now(ZoneId.of("UTC")));
        signature.setActive(true);
        signature.setCompany(company);

        signatureRepository.saveAndFlush(signature);

    }

    private void createApprovedProduct(){
        ApprovedProduct p1 = new ApprovedProduct();
        p1.setProductCode("P0001");
        p1.setDescription("Producto 0001");
        p1.setCompany(company);
        p1.setProductService(productServiceRepository.findAll().get(0));
        p1.setMeasurementUnit(unitRepository.findAll().get(0));

        ApprovedProduct p2 = new ApprovedProduct();
        p2.setProductCode("P0002");
        p2.setDescription("Producto 0002");
        p2.setCompany(company);
        p2.setProductService(productServiceRepository.findAll().get(0));
        p2.setMeasurementUnit(unitRepository.findAll().get(0));

        ApprovedProduct p3 = new ApprovedProduct();
        p3.setProductCode("P0003");
        p3.setDescription("Producto 0003");
        p3.setCompany(company);
        p3.setProductService(productServiceRepository.findAll().get(0));
        p3.setMeasurementUnit(unitRepository.findAll().get(0));

        approvedProductRepository.save(p1);
        approvedProductRepository.save(p2);
        approvedProductRepository.save(p3);
    }

    private InvoiceIssueReq createInvoiceIssue() {
        InvoiceIssueReq request = new InvoiceIssueReq();

        request.setCompanyId(company.getId());
        request.setBranchOfficeSiat(0);
        request.setDocumentSectorType(XmlSectorType.INVOICE_COMPRA_VENTA);
        request.setPointSaleSiat(0);

        request.setHeader(createHeader());
        request.setDetail(createDetail());

        return request;
    }

    private List<Map<String,String>> createDetail() {
        List<Map<String,String>> list = new ArrayList<>();

        HashMap<String, String> detalle = new HashMap<>();
        detalle.put("codigoProducto","P0001");
        detalle.put("descripcion","Mantenimiento de Software");
        detalle.put("cantidad","10");
        detalle.put("precioUnitario","2.5");
        detalle.put("subTotal","25");

        list.add(detalle);

        return list;
    }

    private HashMap<String, Object> createHeader(){
        HashMap<String,Object> header = new HashMap<>();
        header.put("numeroFactura","100");
        header.put("nombreRazonSocial","Pablo Mamani");
        header.put("codigoTipoDocumentoIdentidad","1");
        header.put("numeroDocumento","1548971");
        header.put("complemento","ABC");
        header.put("codigoCliente","Pmamani");
        header.put("codigoMetodoPago","1");
        header.put("montoTotal","25");
        header.put("montoTotalSujetoIva","25");
        header.put("codigoMoneda","1");
        header.put("tipoCambio","1");
        header.put("montoTotalMoneda","25");
        header.put("leyenda","Ley 453: Tienes derecho a recibir info sobre las caracteristicas y contenidos de los servicios que utilices.");
        header.put("usuario","pperez");
        return header ;
    }

    @Test
    public void test() throws XmlFailedConversionException {
        createCompany();
        System.out.println("idCompany:" + company.getId());
        createBranchOffice();
        createPoinOfSale();
        createSignature();

        //syncCodeService.synchronize(company.getId());

        SyncParameterReq req= new SyncParameterReq();
        req.setCompanyId(company.getId());
        req.setBranchOfficeSiat(0);
        req.setPointSaleSiat(0);
        //syncParameterService.synchronize(req);

        List<ProductService> listProductService = productServiceRepository.findAll();
        List<MeasurementUnit>listMeasurement = unitRepository.findAll();

        createApprovedProduct();

        InvoiceIssueReq invoiceIssueReq = createInvoiceIssue() ;

        XmlBaseInvoice baseInvoice = XmlInvoiceAdapter.convert(invoiceIssueReq, company.getModalitySiat(), legendRepository);

        StopWatch watch = new StopWatch("test");
        InvoiceRequest record = new InvoiceRequest();
        record.setId(1L);

        InvoiceIssueRes response = issueService.emit(invoiceIssueReq, baseInvoice, record.getId(), watch);

        System.out.println(response.getMessage());

        assertThat(response.getCode()).isEqualTo(SiatResponseCodes.SUCCESS);
    }

}
