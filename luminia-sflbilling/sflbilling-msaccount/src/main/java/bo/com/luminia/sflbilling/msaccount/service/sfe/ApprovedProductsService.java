package bo.com.luminia.sflbilling.msaccount.service.sfe;

import bo.com.luminia.sflbilling.msaccount.repository.ApprovedProductRepository;
import bo.com.luminia.sflbilling.msaccount.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msaccount.repository.MeasurementUnitRepository;
import bo.com.luminia.sflbilling.msaccount.repository.ProductServiceRepository;
import bo.com.luminia.sflbilling.domain.ApprovedProduct;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.MeasurementUnit;
import bo.com.luminia.sflbilling.domain.ProductService;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec.DefaultTransactionException;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.admin.ApprovedProductReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.ApprovedProductCreateExternalReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.ApprovedProductCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.ApprovedProductUpdateExternalReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.ApprovedProductUpdateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.CrudRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.ApprovedProductDomainRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.ApprovedProductExternalRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.ApprovedProductRes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ApprovedProductsService implements BaseCrudService<ApprovedProductCreateReq, ApprovedProductUpdateReq, CrudRes> {

    private final ApprovedProductRepository repository;
    private final CompanyRepository companyRepository;
    private final MeasurementUnitRepository measurementUnitRepository;
    private final ProductServiceRepository productServiceRepository;

    private CrudRes createResponse(Integer code, String message, Object body) {
        CrudRes response = new CrudRes();
        response.setCode(code);
        response.setMessage(message);
        response.setBody(body);
        return response;
    }

    @Override
    public CrudRes create(ApprovedProductCreateReq request) {
        CrudRes response = new CrudRes();
        response.setCode(ResponseCodes.WARNING);
        response.setMessage(ResponseMessages.WARNING_OPERACION_NO_EJECUTADA);

        if (request.getCompanyId() != null) {
            Optional<Company> companyFromDb = companyRepository.findByIdAndActiveTrue(request.getCompanyId());
            if (!companyFromDb.isPresent()) {
                throw new DefaultTransactionException(String.format(ResponseMessages.ERROR_EMPRESA_NO_ENCONTRADA,
                    request.getCompanyId()), "approvedProducts", ErrorKeys.ERR_RECORD_NOT_FOUND);
                //return createResponse(ResponseCodes.ERROR, String.format(ResponseMessages.ERROR_EMPRESA_NO_ENCONTRADA, request.getCompanyId()), null);
            }
            ApprovedProduct entity = new ApprovedProduct();

            if (request.getProductServiceId() != null) {
                Optional<ProductService> fromDb = productServiceRepository.findById(request.getProductServiceId());
                if (fromDb.isPresent()) {
                    entity.setProductService(fromDb.get());
                } else {
                    //return createResponse(ResponseCodes.ERROR, String.format(ResponseMessages.ERROR_NO_EXISTE_PRODUCT_SERVICE, request.getProductServiceId()), null);
                    throw new DefaultTransactionException(
                        String.format(ResponseMessages.ERROR_NO_EXISTE_PRODUCT_SERVICE, request.getProductServiceId()),
                        ErrorEntities.APPROVED_PRODUCTS,
                        ErrorKeys.ERR_RECORD_NOT_FOUND);
                }
            } else {
                throw new DefaultTransactionException(
                    String.format(ResponseMessages.ERROR_CAMPO_REQUERIDO, "productServiceId"),
                    ErrorEntities.APPROVED_PRODUCTS,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);
                //return createResponse(ResponseCodes.ERROR, String.format(ResponseMessages.ERROR_CAMPO_REQUERIDO, "productServiceId"), null);
            }

            if (request.getMeasurementUnitId() != null) {
                Optional<MeasurementUnit> fromDb = measurementUnitRepository.findById(request.getMeasurementUnitId());
                if (fromDb.isPresent()) {
                    entity.setMeasurementUnit(fromDb.get());
                } else {
                    throw new DefaultTransactionException(
                        String.format(ResponseMessages.ERROR_NO_EXISTE_MEASUREMENT_UNIT, request.getMeasurementUnitId()),
                        ErrorEntities.MEASUREMENT_UNIT,
                        ErrorKeys.ERR_RECORD_NOT_FOUND);
                    //return createResponse(ResponseCodes.ERROR, String.format(ResponseMessages.ERROR_NO_EXISTE_MEASUREMENT_UNIT, request.getMeasurementUnitId()), null);
                }
            } else {
                throw new DefaultTransactionException(
                    String.format(ResponseMessages.ERROR_CAMPO_REQUERIDO, "measurementUnidId"),
                    ErrorEntities.MEASUREMENT_UNIT,
                    ErrorKeys.ERR_REQUIRED_VALUE);
                //return createResponse(ResponseCodes.ERROR, String.format(ResponseMessages.ERROR_CAMPO_REQUERIDO, "measurementUnidId"), null);
            }

            entity.setCompany(companyFromDb.get());
            entity.setDescription(request.getDescription());
            entity.setProductCode(request.getProductCode());

            repository.save(entity);
            repository.flush();

            response.setCode(ResponseCodes.SUCCESS);
            response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
            response.setBody(new Object() {
                public final long id = entity.getId();
            });
        }

        return response;
    }

    public CrudRes createExternal(Company company, ApprovedProductCreateExternalReq request) {
        CrudRes response = new CrudRes();
        response.setCode(ResponseCodes.WARNING);
        response.setMessage(ResponseMessages.WARNING_OPERACION_NO_EJECUTADA);

        ApprovedProduct entity = new ApprovedProduct();

        if (request.getProductService() != null) {
            Optional<ProductService> fromDb = productServiceRepository.findFirstBySiatIdAndCompanyIdAndActiveIsTrue(request.getProductService(), company.getId().intValue());
            if (fromDb.isPresent()) {
                entity.setProductService(fromDb.get());
            } else {
                throw new DefaultTransactionException(
                    String.format(ResponseMessages.ERROR_NO_EXISTE_PRODUCT_SERVICE, request.getProductService()),
                    ErrorEntities.APPROVED_PRODUCTS,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);
            }
        } else {
            throw new DefaultTransactionException(
                String.format(ResponseMessages.ERROR_CAMPO_REQUERIDO, "productService"),
                ErrorEntities.APPROVED_PRODUCTS,
                ErrorKeys.ERR_RECORD_NOT_FOUND);
        }

        if (request.getMeasurementUnit() != null) {
            Optional<MeasurementUnit> fromDb = measurementUnitRepository.findFirstBySiatIdAndCompanyIdAndActiveIsTrue(request.getMeasurementUnit(), company.getId().intValue());
            if (fromDb.isPresent()) {
                entity.setMeasurementUnit(fromDb.get());
            } else {
                throw new DefaultTransactionException(
                    String.format(ResponseMessages.ERROR_NO_EXISTE_MEASUREMENT_UNIT, request.getMeasurementUnit()),
                    ErrorEntities.MEASUREMENT_UNIT,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);
            }
        } else {
            throw new DefaultTransactionException(
                String.format(ResponseMessages.ERROR_CAMPO_REQUERIDO, "measurementUnit"),
                ErrorEntities.MEASUREMENT_UNIT,
                ErrorKeys.ERR_REQUIRED_VALUE);
        }

        entity.setCompany(company);
        entity.setDescription(request.getDescription());
        entity.setProductCode(request.getProductCode());

        repository.save(entity);
        repository.flush();

        response.setCode(ResponseCodes.SUCCESS);
        response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
        response.setBody(new Object() {
            public final long id = entity.getId();
        });

        return response;
    }

    @Transactional
    public CrudRes createExternal(Company company, List<ApprovedProductCreateExternalReq> listRequest) {
        CrudRes response = new CrudRes();
        response.setCode(ResponseCodes.WARNING);
        response.setMessage(ResponseMessages.WARNING_OPERACION_NO_EJECUTADA);

        List<ApprovedProductExternalRes> listResults = new ArrayList<>();

        for (ApprovedProductCreateExternalReq request : listRequest) {
            ApprovedProduct entity = new ApprovedProduct();

            if (request.getProductService() != null) {
                Optional<ProductService> fromDb = productServiceRepository.findFirstBySiatIdAndCompanyIdAndActiveIsTrue(request.getProductService(), company.getId().intValue());
                if (fromDb.isPresent()) {
                    entity.setProductService(fromDb.get());
                } else {
                    throw new DefaultTransactionException(
                        String.format(ResponseMessages.ERROR_NO_EXISTE_PRODUCT_SERVICE, request.getProductService()),
                        ErrorEntities.APPROVED_PRODUCTS,
                        ErrorKeys.ERR_RECORD_NOT_FOUND);
                }
            } else {
                throw new DefaultTransactionException(
                    String.format(ResponseMessages.ERROR_CAMPO_REQUERIDO, "productService"),
                    ErrorEntities.APPROVED_PRODUCTS,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);
            }

            if (request.getMeasurementUnit() != null) {
                Optional<MeasurementUnit> fromDb = measurementUnitRepository.findFirstBySiatIdAndCompanyIdAndActiveIsTrue(request.getMeasurementUnit(), company.getId().intValue());
                if (fromDb.isPresent()) {
                    entity.setMeasurementUnit(fromDb.get());
                } else {
                    throw new DefaultTransactionException(
                        String.format(ResponseMessages.ERROR_NO_EXISTE_MEASUREMENT_UNIT, request.getMeasurementUnit()),
                        ErrorEntities.MEASUREMENT_UNIT,
                        ErrorKeys.ERR_RECORD_NOT_FOUND);
                }
            } else {
                throw new DefaultTransactionException(
                    String.format(ResponseMessages.ERROR_CAMPO_REQUERIDO, "measurementUnit"),
                    ErrorEntities.MEASUREMENT_UNIT,
                    ErrorKeys.ERR_REQUIRED_VALUE);
            }

            entity.setCompany(company);
            entity.setDescription(request.getDescription());
            entity.setProductCode(request.getProductCode());

            repository.save(entity);
            repository.flush();

            listResults.add(new ApprovedProductExternalRes(entity.getId(), request.getProductCode(),
                request.getDescription(), request.getProductService(), request.getMeasurementUnit()));
        }

        response.setCode(ResponseCodes.SUCCESS);
        response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
        response.setBody(listResults);

        return response;
    }

    @Override
    public CrudRes delete(Long companyId, Integer id) {
        throw new NotImplementedException("Metodo no implementado");
    }

    public CrudRes delete(Long id) {
        if (id != null) {
            Optional<ApprovedProduct> fromDb = repository.findById(id);
            if (fromDb.isPresent()) {
                ApprovedProduct entity = fromDb.get();
                repository.delete(entity);
                repository.flush();
                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_ELIMINACION_EXITOSA, null);
            } else {
                throw new DefaultTransactionException(
                    String.format(ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, "measurementUnidId"),
                    ErrorEntities.APPROVED_PRODUCTS,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);
                //return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }
        } else {
            throw new DefaultTransactionException(
                String.format(ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID, "Id"),
                ErrorEntities.APPROVED_PRODUCTS,
                ErrorKeys.ERR_REQUIRED_VALUE);

            //return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID, null);
        }
    }

    public CrudRes deleteExternal(Company company, Long id) {
        if (id != null) {
            Optional<ApprovedProduct> fromDb = repository.findByCompanyIdAndId(company.getId(), id);
            if (fromDb.isPresent()) {
                ApprovedProduct entity = fromDb.get();

                List listProducts = repository.findAllByCompanyIdAndProductCode(company.getId(), entity.getProductCode());
                if (listProducts.size() != 0) {
                    throw new DefaultTransactionException(
                        String.format(ResponseMessages.ERROR_EXIST_APPROVED_PRODUCT_INVOICE, entity.getProductCode()),
                        ErrorEntities.APPROVED_PRODUCTS,
                        ErrorKeys.ERR_RECORD_ALREADY_EXISTS);
                }

                repository.delete(entity);
                repository.flush();
                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_ELIMINACION_EXITOSA, null);
            } else {
                throw new DefaultTransactionException(
                    String.format(ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, "id"),
                    ErrorEntities.APPROVED_PRODUCTS,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);
            }
        } else {
            throw new DefaultTransactionException(
                String.format(ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID, "Id"),
                ErrorEntities.APPROVED_PRODUCTS,
                ErrorKeys.ERR_REQUIRED_VALUE);
        }
    }

    @Override
    public CrudRes update(ApprovedProductUpdateReq request) {

        if (request.getId() != null) {
            Optional<ApprovedProduct> result = repository.findByCompanyIdAndId(request.getCompanyId(), request.getId());
            if (result.isPresent()) {
                ApprovedProduct entity = result.get();
                if (request.getProductCode() != null) {
                    entity.setProductCode(request.getProductCode());
                }
                if (request.getDescription() != null) {
                    entity.setDescription(request.getDescription());
                }

                if (request.getProductServiceId() != null) {
                    Optional<ProductService> fromDb = productServiceRepository.findById(request.getProductServiceId());
                    if (fromDb.isPresent()) {
                        entity.setProductService(fromDb.get());
                    } else {
                        throw new DefaultTransactionException(
                            String.format(ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID, "measurementUnidId"),
                            ErrorEntities.MEASUREMENT_UNIT,
                            ErrorKeys.ERR_REQUIRED_VALUE);
                        //return createResponse(ResponseCodes.ERROR, String.format(ResponseMessages.ERROR_NO_EXISTE_PRODUCT_SERVICE, request.getProductServiceId()), null);
                    }
                }

                if (request.getMeasurementUnitId() != null) {
                    Optional<MeasurementUnit> fromDb = measurementUnitRepository.findById(request.getMeasurementUnitId());
                    if (fromDb.isPresent()) {
                        entity.setMeasurementUnit(fromDb.get());
                    } else {
                        throw new DefaultTransactionException(
                            String.format(ResponseMessages.ERROR_NO_EXISTE_MEASUREMENT_UNIT, request.getMeasurementUnitId()),
                            ErrorEntities.APPROVED_PRODUCTS,
                            ErrorKeys.ERR_RECORD_NOT_FOUND);
                        //return createResponse(ResponseCodes.ERROR, String.format(ResponseMessages.ERROR_NO_EXISTE_MEASUREMENT_UNIT, request.getMeasurementUnitId()), null);
                    }
                }

                if (request.getCompanyId() != null) {
                    Optional<Company> fromDb = companyRepository.findByIdAndActiveTrue(request.getCompanyId());
                    if (fromDb.isPresent()) {
                        entity.setCompany(fromDb.get());
                    } else {
                        throw new DefaultTransactionException(
                            String.format(ResponseMessages.ERROR_REGISTRO_COMPANY_NO_ENCONTRADO, request.getCompanyId()),
                            ErrorEntities.APPROVED_PRODUCTS,
                            ErrorKeys.ERR_REQUIRED_VALUE);
                        //return createResponse(ResponseCodes.ERROR,
                        //    String.format(ResponseMessages.ERROR_REGISTRO_COMPANY_NO_ENCONTRADO, request.getCompanyId()), null);
                    }
                }

                repository.save(entity);
                repository.flush();

                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_ACTUALIZACION_EXITOSA,
                    new Object() {
                        public final long id = entity.getId();
                    });
            } else {
                throw new DefaultTransactionException(
                    String.format(ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, request.getCompanyId()),
                    ErrorEntities.APPROVED_PRODUCTS,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);
                //return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }
        } else {
            return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID, null);
        }
    }

    public CrudRes updateExternal(Company company, ApprovedProductUpdateExternalReq request) {
        if (request.getId() != null) {
            Optional<ApprovedProduct> approvedProduct = repository.findByCompanyIdAndId(company.getId(), request.getId());
            if (approvedProduct.isPresent()) {
                ApprovedProduct entity = approvedProduct.get();

                List listProducts = repository.findAllByCompanyIdAndProductCode(company.getId(), entity.getProductCode());
                if (listProducts.size() != 0) {
                    throw new DefaultTransactionException(
                        String.format(ResponseMessages.ERROR_EXIST_APPROVED_PRODUCT_INVOICE, entity.getProductCode()),
                        ErrorEntities.APPROVED_PRODUCTS,
                        ErrorKeys.ERR_RECORD_ALREADY_EXISTS);
                }

                if (request.getProductCode() != null) {
                    entity.setProductCode(request.getProductCode());
                }
                if (request.getDescription() != null) {
                    entity.setDescription(request.getDescription());
                }
                if (request.getProductService() != null) {
                    Optional<ProductService> fromDb = productServiceRepository.findFirstBySiatIdAndCompanyIdAndActiveIsTrue(request.getProductService(), company.getId().intValue());
                    if (fromDb.isPresent()) {
                        entity.setProductService(fromDb.get());
                    } else {
                        throw new DefaultTransactionException(
                            String.format(ResponseMessages.ERROR_NO_EXISTE_PRODUCT_SERVICE, request.getProductService()),
                            ErrorEntities.MEASUREMENT_UNIT,
                            ErrorKeys.ERR_REQUIRED_VALUE);
                    }
                }
                if (request.getMeasurementUnit() != null) {
                    Optional<MeasurementUnit> fromDb = measurementUnitRepository.findFirstBySiatIdAndCompanyIdAndActiveIsTrue(request.getMeasurementUnit(), company.getId().intValue());
                    if (fromDb.isPresent()) {
                        entity.setMeasurementUnit(fromDb.get());
                    } else {
                        throw new DefaultTransactionException(
                            String.format(ResponseMessages.ERROR_NO_EXISTE_MEASUREMENT_UNIT, request.getMeasurementUnit()),
                            ErrorEntities.APPROVED_PRODUCTS,
                            ErrorKeys.ERR_RECORD_NOT_FOUND);
                    }
                }

                repository.save(entity);
                repository.flush();

                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_ACTUALIZACION_EXITOSA,
                    new Object() {
                        public final long id = entity.getId();
                    });
            } else {
                throw new DefaultTransactionException(
                    String.format(ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, request.getId()),
                    ErrorEntities.APPROVED_PRODUCTS,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);
            }
        } else {
            return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID, null);
        }
    }

    @Override
    public CrudRes get(Long companyId, Integer id) {
        if (id != null && companyId != null) {
            Optional<ApprovedProduct> entity = repository.findByCompanyIdAndId(companyId, new Long(id));
            if (entity.isPresent()) {
                ApprovedProduct approvedProduct = entity.get();
                ApprovedProductRes body = new ApprovedProductRes();
                BeanUtils.copyProperties(approvedProduct, body);

                body.setProductService(approvedProduct.getProductService().getId());
                body.setProductServiceName(approvedProduct.getProductService().getDescription());

                body.setMeasurementUnit(approvedProduct.getMeasurementUnit().getId());
                body.setMeasurementUnitName(approvedProduct.getMeasurementUnit().getDescription());

                body.setCompany(approvedProduct.getCompany().getId());

                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, body);
            } else {
                return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }
        } else {
            throw new DefaultTransactionException(
                (ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO),
                ErrorEntities.APPROVED_PRODUCTS,
                ErrorKeys.ERR_RECORD_NOT_FOUND);
        }
    }

    public CrudRes getExternal(Long companyId, Long id) {
        if (id != null && companyId != null) {
            Optional<ApprovedProduct> entity = repository.findByCompanyIdAndId(companyId, id);
            if (entity.isPresent()) {
                ApprovedProduct approvedProduct = entity.get();
                ApprovedProductDomainRes body = new ApprovedProductDomainRes();
                BeanUtils.copyProperties(approvedProduct, body);

                body.setProductService(approvedProduct.getProductService().getSiatId());
                body.setProductServiceName(approvedProduct.getProductService().getDescription());

                body.setMeasurementUnit(approvedProduct.getMeasurementUnit().getSiatId());
                body.setMeasurementUnitName(approvedProduct.getMeasurementUnit().getDescription());

                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, body);
            } else {
                return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }
        } else {
            throw new DefaultTransactionException(
                (ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO),
                ErrorEntities.APPROVED_PRODUCTS,
                ErrorKeys.ERR_RECORD_NOT_FOUND);
        }
    }

    public CrudRes getByProductCode(Long companyId, String productCode) {
        if (companyId != null && productCode != null) {
            Optional<ApprovedProduct> fromDb = repository.findByCompanyIdAndProductCode(companyId, productCode);
            if (fromDb.isPresent()) {
                ApprovedProductRes response = copy(fromDb.get());
                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, response);
            } else {
                return createResponse(ResponseCodes.WARNING, ResponseMessages.WARNING_LISTA_VACIA, null);
            }
        } else {
            throw new DefaultTransactionException(
                (ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO),
                ErrorEntities.APPROVED_PRODUCTS,
                ErrorKeys.ERR_RECORD_NOT_FOUND);
        }
    }

    public CrudRes getAll(Long companyId, Integer id) {
        if (id != null && companyId != null) {
            List<ApprovedProduct> entity = repository.findAllByCompanyId(companyId);
            if (!entity.isEmpty()) {
                List<ApprovedProductRes> toBody = new ArrayList<>();
                for (ApprovedProduct approvedProduct : entity) {
                    ApprovedProductRes body = copy(approvedProduct);
                    toBody.add(body);
                }
                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, toBody);
            } else {
                throw new DefaultTransactionException(
                    (ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO),
                    ErrorEntities.APPROVED_PRODUCTS,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);

                //return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }
        } else {
            return createResponse(ResponseCodes.WARNING, ResponseMessages.WARNING_LISTA_VACIA, null);
        }
    }

    public CrudRes getAllByCompanyId(Long companyId) {
        List<ApprovedProduct> entity = repository.findAllByCompanyId(companyId);
        if (!entity.isEmpty()) {
            List<ApprovedProductDomainRes> toBody = new ArrayList<>();
            for (ApprovedProduct approvedProduct : entity) {
                ApprovedProductDomainRes body = this.copyDomain(approvedProduct);
                toBody.add(body);
            }
            return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, toBody);
        } else {
            return createResponse(ResponseCodes.WARNING, ResponseMessages.WARNING_LISTA_VACIA, null);
        }
    }

    public CrudRes getByProductService(Long companyId, Long productServiceId) {
        if (productServiceId != null && companyId != null) {
            List<ApprovedProduct> entity = repository.findAllByCompanyIdAndProductServiceId(companyId, productServiceId);
            if (!entity.isEmpty()) {
                List<ApprovedProductRes> toBody = new ArrayList<>();
                for (ApprovedProduct approvedProduct : entity) {
                    ApprovedProductRes body = new ApprovedProductRes();
                    BeanUtils.copyProperties(approvedProduct, body);

                    body.setProductService(approvedProduct.getProductService().getId());
                    body.setProductServiceName(approvedProduct.getProductService().getDescription());

                    body.setMeasurementUnit(approvedProduct.getMeasurementUnit().getId());
                    body.setMeasurementUnitName(approvedProduct.getMeasurementUnit().getDescription());

                    toBody.add(body);
                    body.setCompany(approvedProduct.getCompany().getId());
                }

                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, toBody);
            } else {
                return createResponse(ResponseCodes.WARNING, ResponseMessages.WARNING_LISTA_VACIA, null);
            }
        } else {
            throw new DefaultTransactionException(
                (ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO),
                ErrorEntities.APPROVED_PRODUCTS,
                ErrorKeys.ERR_RECORD_NOT_FOUND);
        }
    }

    public CrudRes getByMeasurementUnit(Long companyId, Long measurementUnitId) {
        if (measurementUnitId != null && companyId != null) {
            List<ApprovedProduct> entity = repository.findAllByCompanyIdAndMeasurementUnitId(companyId, measurementUnitId);
            if (!entity.isEmpty()) {
                List<ApprovedProductRes> toBody = new ArrayList<>();
                for (ApprovedProduct approvedProduct : entity) {
                    ApprovedProductRes body = new ApprovedProductRes();
                    BeanUtils.copyProperties(approvedProduct, body);

                    body.setProductService(approvedProduct.getProductService().getId());
                    body.setProductServiceName(approvedProduct.getProductService().getDescription());

                    body.setMeasurementUnit(approvedProduct.getMeasurementUnit().getId());
                    body.setMeasurementUnitName(approvedProduct.getMeasurementUnit().getDescription());

                    toBody.add(body);
                    body.setCompany(approvedProduct.getCompany().getId());
                }

                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, toBody);
            } else {
                return createResponse(ResponseCodes.WARNING, ResponseMessages.WARNING_LISTA_VACIA, null);
            }
        } else {
            throw new DefaultTransactionException(
                (ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO),
                ErrorEntities.APPROVED_PRODUCTS,
                ErrorKeys.ERR_RECORD_NOT_FOUND);
        }
    }

    private ApprovedProductRes copy(ApprovedProduct ap) {
        ApprovedProductRes body = new ApprovedProductRes();
        BeanUtils.copyProperties(ap, body);

        body.setProductService(ap.getProductService().getId());
        body.setProductServiceName(ap.getProductService().getDescription());

        body.setMeasurementUnit(ap.getMeasurementUnit().getId());
        body.setMeasurementUnitName(ap.getMeasurementUnit().getDescription());
        body.setCompany(ap.getCompany().getId());
        return body;
    }

    private ApprovedProductDomainRes copyDomain(ApprovedProduct ap) {
        ApprovedProductDomainRes body = new ApprovedProductDomainRes();
        BeanUtils.copyProperties(ap, body);

        body.setProductService(ap.getProductService().getSiatId());
        body.setProductServiceName(ap.getProductService().getDescription());

        body.setMeasurementUnit(ap.getMeasurementUnit().getSiatId());
        body.setMeasurementUnitName(ap.getMeasurementUnit().getDescription());
        return body;
    }

    @Transactional
    public ApprovedProductReq save(ApprovedProductReq request, Company company, ProductService productService, MeasurementUnit measurementUnit) {
        ApprovedProduct approvedProduct = this.toEntity(request, company, productService, measurementUnit);
        approvedProduct = repository.save(approvedProduct);
        return this.toDto(approvedProduct);
    }

    @Transactional
    public Optional<ApprovedProductReq> partialUpdate(ApprovedProductReq request, Company company, ProductService productService, MeasurementUnit measurementUnit) {
        return repository
            .findById(request.getId())
            .map(entity -> {
                if (request.getDescription() != null) {
                    entity.setDescription(request.getDescription());
                }
                if (request.getProductCode() != null) {
                    entity.setProductCode(request.getProductCode());
                }
                if (request.getCompanyId() != null) {
                    entity.setCompany(company);
                }
                if (request.getProductServiceId() != null) {
                    entity.setProductService(productService);
                }
                if (request.getMeasurementUnitId() != null) {
                    entity.setMeasurementUnit(measurementUnit);
                }
                log.debug("Request to partially update ApprovedProduct : {}", request);
                return entity;
            })
            .map(repository::save)
            .map(this::toDto);
    }

    public Page<ApprovedProductReq> findAll(Pageable pageable) {
        log.debug("Request to get all ApprovedProduct");
        return repository.findAll(pageable).map(this::toDto);
    }

    public Optional<ApprovedProductReq> findOne(Long id) {
        log.debug("Request to get ApprovedProduct : {}", id);
        return repository.findById(id).map(this::toDto);
    }

    @Transactional
    public void deleteApprovedProduct(Long id) {
        log.debug("Request to delete ApprovedProductReq : {}", id);
        repository.deleteById(id);
    }

    public ApprovedProduct toEntity(ApprovedProductReq dto, Company company, ProductService productService, MeasurementUnit measurementUnit) {
        if (dto == null) {
            return null;
        }

        ApprovedProduct entity = new ApprovedProduct();

        entity.setId(dto.getId());
        entity.setProductCode(dto.getProductCode());
        entity.setDescription(dto.getDescription());
        entity.setCompany(company);
        entity.setProductService(productService);
        entity.setMeasurementUnit(measurementUnit);

        return entity;
    }

    public ApprovedProductReq toDto(ApprovedProduct dto) {
        if (dto == null) {
            return null;
        }

        ApprovedProductReq entity = new ApprovedProductReq();

        entity.setId(dto.getId());
        entity.setProductCode(dto.getProductCode());
        entity.setDescription(dto.getDescription());
        entity.setCompanyId(dto.getCompany().getId());
        entity.setProductServiceId(dto.getProductService().getId());
        entity.setMeasurementUnitId(dto.getMeasurementUnit().getId());

        return entity;
    }
}
