# SFL Billing Common

This library constains
1. Domain for SFL Billing
2. Siat soat mapping classes

### Install library
```
./mvnw -U clean install
```

# Mapping Soap Services
### SFC
```
wsimport -keep -p bo.gob.impuestos.sfe.code https://pilotosiatservicios.impuestos.gob.bo/v2/FacturacionCodigos?wsdl -s src/main/java/
```
```
wsimport -keep -p bo.gob.impuestos.sfe.operation https://pilotosiatservicios.impuestos.gob.bo/v2/FacturacionOperaciones?wsdl -s src/main/java/
```
```
wsimport -keep -p bo.gob.impuestos.sfe.synchronization https://pilotosiatservicios.impuestos.gob.bo/v2/FacturacionSincronizacion?wsdl -s src/main/java/
```
```
wsimport -keep -p bo.gob.impuestos.sfe.electronicinvoice https://pilotosiatservicios.impuestos.gob.bo/v2/ServicioFacturacionElectronica?wsdl -s src/main/java/
```
```
wsimport -keep -p bo.gob.impuestos.sfe.financialinvoice https://pilotosiatservicios.impuestos.gob.bo/v2/ServicioFacturacionEntidadFinanciera?wsdl -s src/main/java/
```
```
wsimport -keep -p bo.gob.impuestos.sfe.basicserviceinvoice https://pilotosiatservicios.impuestos.gob.bo/v2/ServicioFacturacionServicioBasico?wsdl -s src/main/java/
```
```
wsimport -keep -p bo.gob.impuestos.sfe.telecommunicationinvoice https://pilotosiatservicios.impuestos.gob.bo/v2/ServicioFacturacionTelecomunicaciones?wsdl -s src/main/java/
```
```
wsimport -keep -p bo.gob.impuestos.sfe.creditdebitnoteinvoice https://pilotosiatservicios.impuestos.gob.bo/v2/ServicioFacturacionDocumentoAjuste?wsdl -s src/main/java/
```
```
wsimport -keep -p bo.gob.impuestos.sfe.computerizedinvoice https://pilotosiatservicios.impuestos.gob.bo/v2/ServicioFacturacionComputarizada?wsdl -s src/main/java/
```
```
wsimport -keep -p bo.gob.impuestos.sfe.buysellinvoice https://pilotosiatservicios.impuestos.gob.bo/v2/ServicioFacturacionCompraVenta?wsdl -s src/main/java/
```
```
wsimport -keep -p bo.gob.impuestos.sfe.airticketinvoice https://pilotosiatservicios.impuestos.gob.bo/v2/ServicioFacturacionBoletoAereo?wsdl -s src/main/java/
```
