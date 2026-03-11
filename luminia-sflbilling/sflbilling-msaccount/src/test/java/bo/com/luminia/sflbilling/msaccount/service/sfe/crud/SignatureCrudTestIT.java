package bo.com.luminia.sflbilling.msaccount.service.sfe.crud;

import bo.com.luminia.sflbilling.msaccount.service.sfe.SignatureService;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.SignatureCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.SignatureUpdateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.CrudRes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SignatureCrudTestIT {

    @Autowired
    private SignatureService service ;

    private SignatureCreateReq create() {
        SignatureCreateReq response = new SignatureCreateReq();

        response.setCompanyId(1001L);
        response.setCertificate("-----BEGIN CERTIFICATE-----\n" +
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
            "-----END CERTIFICATE-----");
        response.setPrivateKey("-----BEGIN PRIVATE KEY-----\n" +
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
        response.setStartDate("2021-07-14");
        response.setEndDate("2021-08-14");

        return response;
    }

    private SignatureUpdateReq update() {
        SignatureUpdateReq response = new SignatureUpdateReq();

        response.setId(1001L);
        response.setCompanyId(1001L);
        response.setCertificate("-----BEGIN CERTIFICATE-----\n" +
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
            "-----END CERTIFICATE-----");
        response.setPrivateKey("-----BEGIN PRIVATE KEY-----\n" +
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
        response.setStartDate("2021-07-14");
        response.setEndDate("2021-08-18");

        return response;
    }

    @Test
    public void testCreate (){
        CrudRes response = service.create(create());

        assertThat(response.getCode()).isEqualTo(ResponseCodes.SUCCESS);
    }

    @Test
    public void testGet(){
        CrudRes response = service.get(1001L, 0);

        assertThat(response.getCode()).isEqualTo(ResponseCodes.SUCCESS);
    }

    @Test
    public void testUpdate(){
        CrudRes response = service.update(update());

        assertThat(response.getCode()).isEqualTo(ResponseCodes.SUCCESS);
    }
}
