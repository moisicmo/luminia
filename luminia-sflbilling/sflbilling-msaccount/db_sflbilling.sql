

INSERT INTO public.company (id,nit,"name",business_name,city,phone,address,system_code,package_send,event_send,user_siat,password_siat,environment_siat,modality_siat,created_by,created_date,last_modified_by,last_modified_date) VALUES
	 (1,1028675024,'LUMINIA','LUMINIA','BOLIVIA','78859964','Calle 20 de Octubre','6CE0F4E55826C2DB97038EF',true,true,'LUMINIA2020','Luminia2020bol','TEST','ELECTRONIC_BILLING','admin','2021-07-13 03:19:56.003739',NULL,NULL),
	 (2,1028675024,'LUMINIA','LUMINIA','BOLIVIA','78859964','Calle 20 de Octubre','6CE0F4E1DC78AA3DBC498EF',true,true,'LUMINIA2020','Luminia2020bol','TEST','COMPUTERIZED_BILLING','admin','2021-07-13 03:19:56.263109',NULL,NULL);


INSERT INTO public.branch_office (id,branch_office_siat_id,"name",description,active,company_id) VALUES
	 (1,0,'SUCURSAL MATRIZ','LUMINIA SUCURSAL MATRIZ',true,1),
	 (2,0,'SUCURSAL MATRIZ','LUMINIA2 SUCURSAL MATRIZ',true,2);


INSERT INTO public.point_sale (id,point_sale_siat_id,"name",description,active,branch_office_id) VALUES
	 (1,0,'PUNTO VENTA 0','PUNTO VENTA 0',true,1),
	 (2,1,'PUNTO VENTA 1','PUNTO VENTA 1',true,1),
	 (3,2,'PUNTO VENTA 2','PUNTO VENTA 2',true,1),
	 (4,0,'PUNTO VENTA 0','PUNTO VENTA 0',true,2),
	 (5,1,'PUNTO VENTA 1','PUNTO VENTA 1',true,2),
	 (6,2,'PUNTO VENTA 2','PUNTO VENTA 2',true,2);


INSERT INTO public.signature (id,certificate,private_key,start_date,end_date,active,company_id,created_by,created_date,last_modified_by,last_modified_date) VALUES
	 (1,'-----BEGIN CERTIFICATE-----
MIIEvjCCA6agAwIBAgIIgtMB5t5OwAAwDQYJKoZIhvcNAQEFBQAwgbUxCzAJBgNV
BAYTAkJPMQ8wDQYDVQQIDAZMQSBQQVoxDzANBgNVBAcMBkxBIFBBWjEeMBwGA1UE
CgwVRW50aWRhZCBDZXJ0aWZpY2Fkb3JhMQwwCgYDVQQLDANVSUQxEzARBgNVBAMM
CkFEU0lCIEZBS0UxJDAiBgkqhkiG9w0BCQEWFW5jb2FyaXRlQGFkc2liLmdvYi5i
bzEbMBkGA1UEBRMSNzM1MjQyNDI0NDY0NjM0MjM0MB4XDTIxMDcxNTE0NTgwMloX
DTIxMDgxNDE0NTgwMlowge4xJDAiBgNVBAMTG0VEVUFSRE8gUEVEUk8gQVJBTkRB
IE1PSklDQTEUMBIGA1UEChMLU0lOVEVTSVMgU0ExETAPBgNVBAsTCFNJTlRFU0lT
MRwwGgYDVQQMExNSRVBSRVNFTlRBTlRFIExFR0FMMQswCQYDVQQGEwJCTzELMAkG
A1UELhMCQ0kxFDASBgcrBgEBAQEAEwczMzY5NzM3MREwDwYKCZImiZPyLGQBARMB
MDETMBEGA1UEBRMKMTAyODY3NTAyNDEnMCUGCSqGSIb3DQEJARYYZWR1YXJkb2FA
c2ludGVzaXMuY29tLmJvMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA
vLOP2/W8Ao/lm25JJYh6uQVL7Ic34rS5c2DbLHBH6g4vStAMehDzCPSywzr98f3J
DO30T+4EjZR7DWLXLRQPSOJrTXw7gC/EI1rPFn6RWVLG2uRfeT6uUL6vAt23nHPn
uEeScfvKZbMxD5YcPMhcr6Z/cw67eGWWgIg9EDBqgG+xSnvW7wdeSisEv94ELsQs
ffXknY/OWUD2vsGir42dw7YLVivZ/LWsY0rryLbnBSD0x2A3ffGxJcfXRZV5EYQS
3SjQrDEbLddKX/77giflnUkfwp4EX/srhlS2UWxub8PYe3ZrF/vGppw+5n6mIjtg
yApa7BoOonqZdEg7JKbSiwIDAQABo4GWMIGTMAwGA1UdEwQFMAMBAf8wCwYDVR0P
BAQDAgTwMCMGA1UdEQQcMBqGGGVkdWFyZG9hQHNpbnRlc2lzLmNvbS5ibzBRBgNV
HR8ESjBIMEagRKBChkBodHRwczovL2Rlc2Fycm9sbG8uYWRzaWIuZ29iLmJvL2Rl
c2FfYWdlbmNpYS9saXN0X3Jldm9jYWNpb24uY3JsMA0GCSqGSIb3DQEBBQUAA4IB
AQC0jCxvku0oxSU5KTR06s091OVrt209rr9O1K1lkH7qkgl1zbeXJQzpdZEpwCGJ
TbNB1MvC5lZCTrattkkyinmzSmo+RdHccxDC6OIbsYBXsQtPCRnsnWQ+ACKdlG+m
FTH5nafNmjYYNX2x2aLekBzuB7diUhZ3Xzi/VW7TjzppLFWSDCxUmfvXg5TewNn1
omJ90V+mjYHv5ZZI1Z3+kpwV4qWZkVycxHQQRuswEcUY/aq6DpnxpEB1jKp1Zq9x
bMc8z8QonOe9sH2mJuadeGcMX+kdq8mj1nKfy2ubno90tF/6upqfQSRcLvJKAsOq
hwrZMxuEhU3qcwohv+nhj5js
-----END CERTIFICATE-----','-----BEGIN PRIVATE KEY-----
MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC8s4/b9bwCj+Wb
bkkliHq5BUvshzfitLlzYNsscEfqDi9K0Ax6EPMI9LLDOv3x/ckM7fRP7gSNlHsN
YtctFA9I4mtNfDuAL8QjWs8WfpFZUsba5F95Pq5Qvq8C3becc+e4R5Jx+8plszEP
lhw8yFyvpn9zDrt4ZZaAiD0QMGqAb7FKe9bvB15KKwS/3gQuxCx99eSdj85ZQPa+
waKvjZ3DtgtWK9n8taxjSuvItucFIPTHYDd98bElx9dFlXkRhBLdKNCsMRst10pf
/vuCJ+WdSR/CngRf+yuGVLZRbG5vw9h7dmsX+8amnD7mfqYiO2DIClrsGg6iepl0
SDskptKLAgMBAAECggEAQiKIo+W/DALUzSHc/wuTP29MycwmWQ1QBdEHX87GVZC9
IHJEPS/djeD9GhF92SdCJIbtXmokLF7CtdFTatgh90BrrQ+CZw3WpzF3bQHU/UJW
sXbiw0Klo2UBP3bnmCzDJMHo4IwHxpcD9dt9cAwk9+bZukCKkcrA8bFN/XOKkBJ6
8Gtf5J2FjkixZGbLcV5qmh8RcgqMz0OjqugL9pSfgMMEiuc9yqB1Ak2xGUF78DmP
UctG9HAQ8hLUhupU8UR3aoOj7WtMDpX10CIPvIUsh7+tP68LfilgfZR8YINvltB3
8qk86wRRJ8cwbwibKlz1L8RD7vAv4D+lCTIRMyuYeQKBgQDpXbamPP4yytXgkDDZ
V5vqH5NrCm7Dj64E9EyASkyik/TixwCJyCqR6YDufk0rNem6+Vqy8D99WoxPnK3w
FqC5t50/0tvUnoEtMlddmihMScIQmngPfFG1wBvZT9ycULFbQYDpa22IPsGoykNc
BpuWASgKlmqg23AWEpmLB7HIzwKBgQDPANyubW5QInodTBs92lAgewKHSybZv9x+
fwRjbBoMl2mhOIm3/kMhT4GpfRLi1TqNXpUm71GlG7Gv9u9zLxmYwQYPhDOcbQIY
VxV07dbzbVDb32BVpAXZP3wmHsxlK/eFEkSZcch0XvS4WL1mOm3Jll+8qpFWkVvW
o9GAf0BRhQKBgFjmUFxHw/aJeqyPgWxqiYTI/pm6YbOyGnLctf/xTfxpLNLvSG80
h7MKJwmzp6YcZavKrhiYmTchtW3mnARoOlZFcmwL4Z6/uyoCkXGg9lUJjBpTHgWY
MHByfKluWPZbbxT6gSdqu1E5xwCL/Nkj00Vzr1NJNdmNfseJ0mA6UCnVAoGAB3N5
fhHUUbAcAyf1JxHPpoum+KW83UOptSfvSYDfoypkE/iMBIJzeiR5f1dQMbgJJoOM
DN26a54GlFXoIpZEbposFKzmiq/lzmh8Djxta0+5BGES/6Iqz7oYRur+4nllrHWO
4JMW6xFr76LKFn7t6r6t7YWaO6p5ys0UwnJSJ10CgYEAh7uRi0I571KtI+PR6Ein
ULashkmXBIxv9cJcoQGMzzPjrwJYXkelbYOoyqYU+0yRouR6AnwCLO8UAzWfCr7j
wRnP9ZNfr5QKazUZklRsvIj8KLu0x+ugn3CpuyvDaA9Ij4JCVHTFw4kzAkebE/hT
aKRfChC+HQMStvzHScUZ6yk=
-----END PRIVATE KEY-----
','2021-07-14 00:00:00','2021-08-14 00:00:00',true,1,'admin','2021-07-16 00:34:02.029946',NULL,NULL);


INSERT INTO public.schedule_setting (id,cron_date,description,active,company_id,created_by,created_date,last_modified_by,last_modified_date) VALUES
	 (1,'0 0/60 * 1/1 * ?','Programación calendario',true,1,'admin','2021-07-15 00:43:37.17',NULL,NULL),
	 (2,'0 0/60 * 1/1 * ?','Programación calendario',true,2,'admin','2021-07-15 00:43:37',NULL,NULL);


INSERT INTO public.offline (id,active,created_by,created_date,last_modified_by,last_modified_date) VALUES
	 (1,false,'admin','2021-07-20 06:45:02.782317','admin','2021-07-20 02:20:32.584');


INSERT INTO public.approved_product (id,product_code,description,company_id,product_service_id,measurement_unit_id,created_by,created_date,last_modified_by,last_modified_date) VALUES
	 (1,'P0001','PRODUCTO 1',1,1001,1058,'admin','2021-07-13 03:21:18.465475',NULL,NULL),
	 (2,'P0002','PRODUCTO 2',1,1002,1058,'admin','2021-07-13 03:21:18.465475',NULL,NULL),
	 (3,'ALQ01','INMUEBLE 1',1,1006,1057,'admin','2021-07-13 03:21:18.465475',NULL,NULL),
	 (4,'ALQ02','INMUEBLE 2',1,1007,1057,'admin','2021-07-13 03:21:18.465475',NULL,NULL),
	 (5,'DEV01','DEV 1',1,1008,1058,'admin','2021-07-13 03:21:18.465475',NULL,NULL),
	 (6,'DEV02','DEV 2',1,1009,1058,'admin','2021-07-13 03:21:18.465475',NULL,NULL);

INSERT INTO public.approved_product (id,product_code,description,company_id,product_service_id,measurement_unit_id,created_by,created_date,last_modified_by,last_modified_date) VALUES
	 (7,'P0001','PRODUCTO 1',2,1001,1058,'admin','2021-07-13 03:21:18.465475',NULL,NULL),
	 (8,'P0002','PRODUCTO 2',2,1002,1058,'admin','2021-07-13 03:21:18.465475',NULL,NULL),
	 (9,'ALQ01','INMUEBLE 1',2,1006,1057,'admin','2021-07-13 03:21:18.465475',NULL,NULL),
	 (10,'ALQ02','INMUEBLE 2',2,1007,1057,'admin','2021-07-13 03:21:18.465475',NULL,NULL),
	 (11,'DEV01','DEV 1',2,1008,1058,'admin','2021-07-13 03:21:18.465475',NULL,NULL),
	 (12,'DEV02','DEV 2',2,1009,1058,'admin','2021-07-13 03:21:18.465475',NULL,NULL);
