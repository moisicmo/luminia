# SFL Billing Docker
**Run all containers**
```
docker-compose up -d
```
**Stop and destroy all containers**
```
docker-compose down
```

## Environment global File
Create an **.env** file and use the following properties:
```
# Database
DATASOURCE_URL=jdbc:postgresql://localhost:5432/sflbilling
DATASOURCE_USERNAME=postgres
DATASOURCE_PASSWORD=postgres

# App version
MSACCOUNT_IMAGE_VERSION=latest
MSINVOICE_IMAGE_VERSION=latest
MSREPORT_IMAGE_VERSION=latest
MSBATCH_IMAGE_VERSION=latest
FRONTADMIN_IMAGE_VERSION=latest

# App level log
MSACCOUNT_LEVEL_LOG=(debug|info|error|warn)
MSINVOICE_LEVEL_LOG=(debug|info|error|warn)
MSREPORT_LEVEL_LOG=(debug|info|error|warn)
MSBATCH_LEVEL_LOG=(debug|info|error|warn)

# Enabled cors
CORS_ALLOWED_ORIGINS='http://host:port1,http://host:port2'
CORS_ALLOWED_METHODS='*'
CORS_ALLOWED_HEADERS='*'
CORS_EXPOSED_HEADERS='*'
CORS_ALLOW_CREDENTIALS=false
CORS_MAX_AGE=1800

# Notification Api Endpoint
NOTIFICATION_ENDPOINT=http://199.3.0.227:8080/api/send/mail

# General properties
JWT_BASE64_SECRET=value
SIAT_SOAP_URL=https://pilotosiatservicios.impuestos.gob.bo
SIAT_QR_URL=https://pilotosiat.impuestos.gob.bo

# Batch properties
BATCH_OFFLINE_API_KEY=TokenApi value

# Frontend admin api url
FRONTADMIN_API_URL=http://host:port
```

### SIAT ENVIRONMENT
**Test** (Default)
```
SIAT_SOAP_URL=https://pilotosiatservicios.impuestos.gob.bo
SIAT_QR_URL=https://pilotosiat.impuestos.gob.bo
```
**Production**
```
SIAT_SOAP_URL=https://siatrest.impuestos.gob.bo
SIAT_QR_URL=https://siat.impuestos.gob.bo
```