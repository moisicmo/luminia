package bo.com.luminia.sflbilling.msaccount.service.sfe;

import bo.com.luminia.sflbilling.msaccount.repository.CompanyRepository;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.enumeration.EnvironmentSiatEnum;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import bo.com.luminia.sflbilling.msaccount.service.GenUtil;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.NotFoundAlertException;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec.DefaultTransactionException;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.admin.CompanyReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.CompanyCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.CompanyUpdateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.CrudRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.CompanyRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyService implements BaseCrudService<CompanyCreateReq, CompanyUpdateReq, CrudRes> {

    private final CompanyRepository companyRepository;

    @Transactional
    public CrudRes create(CompanyCreateReq request) {
        Company entity = new Company();
        entity.setBusinessCode(GenUtil.businessCode());
        entity.setAddress(request.getAddress());
        entity.setBusinessName(request.getBusinessName());
        entity.setCity(request.getCity());
        entity.setEnvironmentSiat(EnvironmentSiatEnum.get(request.getEnvironmentSiat()));
        entity.setEventSend(request.getEventSend());
        entity.setModalitySiat(ModalitySiatEnum.get(request.getModalitySiat()));
        entity.setName(request.getName());
        entity.setNit(request.getNit());
        entity.setPackageSend(request.getPackageSend());
        entity.setEmailNotification(request.getEmailNotification());
        entity.setToken(request.getToken());
        entity.setPhone(request.getPhone());
        entity.setSystemCode(request.getSystemCode());
        entity.setActive(true);
        entity.setLogo(request.getLogo());

        CrudRes response = new CrudRes();
        try {
            companyRepository.save(entity);
        } catch (Exception ex) {
            throw new DefaultTransactionException(
                ResponseMessages.ERROR_REGISTRO_NO_GUARDADO,
                ErrorEntities.COMPANY,
                ErrorKeys.ERR_RECORD_NOT_FOUND);
        }

        //log.info("Created Information for Company: {}", entity);

        response.setCode(ResponseCodes.SUCCESS);
        response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
        // TODO debe crearse una clase no un =
        response.setBody(new Object() {
            public final long id = entity.getId();
            public final String businessCode = entity.getBusinessCode();
        });
        return response;
    }

    @Override
    public CrudRes delete(Long companyId, Integer id) {
        return null;
    }

    public CrudRes getAll() {
        List<Company> result = companyRepository.findAll();
        CrudRes response = new CrudRes();
        List<CompanyRes> companyRestList = new ArrayList<>();

        for (Company company : result) {
            CompanyRes companyRes = new CompanyRes();
            BeanUtils.copyProperties(company, companyRes);

            companyRes.setEnvironmentSiat(company.getEnvironmentSiat().getKey().toString());
            companyRes.setModalitySiat(company.getModalitySiat().getKey().toString());
            if (company.getLogo() != null)
                companyRes.setLogo(Base64.getEncoder().encodeToString(company.getLogo()));
            companyRestList.add(companyRes);
        }

        response.setCode(ResponseCodes.SUCCESS);
        response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
        response.setBody(companyRestList);
        return response;
    }

    public CrudRes get(Long id) {
        Optional<Company> result = companyRepository.findById(id);
        CrudRes response = new CrudRes();

        if (result.isPresent()) {
            Company company = result.get();

            CompanyRes companyRes = new CompanyRes();
            BeanUtils.copyProperties(company, companyRes);

            companyRes.setEnvironmentSiat(company.getEnvironmentSiat().getKey().toString());
            companyRes.setModalitySiat(company.getModalitySiat().getKey().toString());
            if (company.getLogo() != null)
                companyRes.setLogo(Base64.getEncoder().encodeToString(company.getLogo()));

            response.setCode(ResponseCodes.SUCCESS);
            response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
            response.setBody(companyRes);

            return response;
        } else {
            throw new DefaultTransactionException(
                ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO,
                ErrorEntities.COMPANY,
                ErrorKeys.ERR_RECORD_NOT_FOUND);
        }
    }

    public CrudRes getByNit(Long nit) {
        List<Company> result = companyRepository.findCompanyByNit(nit);
        List<CompanyRes> body = new ArrayList<>();
        CrudRes response = new CrudRes();
        if (!result.isEmpty()) {
            for (Company company : result) {
                CompanyRes companyRes = new CompanyRes();
                BeanUtils.copyProperties(company, companyRes);
                companyRes.setEnvironmentSiat(company.getEnvironmentSiat().getKey().toString());
                companyRes.setModalitySiat(company.getModalitySiat().getKey().toString());
                if (company.getLogo() != null)
                    companyRes.setLogo(Base64.getEncoder().encodeToString(company.getLogo()));
                body.add(companyRes);
            }
            response.setCode(ResponseCodes.SUCCESS);
            response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
            response.setBody(body);
            return response;
        } else {
            throw new NotFoundAlertException(
                ErrorKeys.ERR_RECORD_NOT_FOUND,
                String.format(ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, "productServiceId")
            );
        }

    }

    public CrudRes update(CompanyUpdateReq request) {
        Optional<Company> result = companyRepository.findById(request.getId());
        CrudRes response = new CrudRes();

        if (result.isPresent()) {
            Company toUpdate = result.get();

            if (request.getEventSend() != null) {
                toUpdate.setEventSend(request.getEventSend());
            }

            if (request.getPackageSend() != null) {
                toUpdate.setPackageSend(request.getPackageSend());
            }

            if (request.getNit() != null) {
                toUpdate.setNit(request.getNit());
            }

            if (request.getName() != null) {
                toUpdate.setName(request.getName());
            }

            if (request.getBusinessName() != null) {
                toUpdate.setBusinessName(request.getBusinessName());
            }

            if (request.getCity() != null) {
                toUpdate.setCity(request.getCity());
            }

            if (request.getPhone() != null) {
                toUpdate.setPhone(request.getPhone());
            }

            if (request.getAddress() != null) {
                toUpdate.setAddress(request.getAddress());
            }

            if (request.getSystemCode() != null) {
                toUpdate.setSystemCode(request.getSystemCode());
            }

            if (request.getEmailNotification() != null) {
                toUpdate.setEmailNotification(request.getEmailNotification());
            }

            if (request.getToken() != null) {
                toUpdate.setToken(request.getToken());
            }

            if (request.getEnvironmentSiat() != null) {
                toUpdate.setEnvironmentSiat(EnvironmentSiatEnum.get(request.getEnvironmentSiat()));
            }

            if (request.getModalitySiat() != null) {
                toUpdate.setModalitySiat(ModalitySiatEnum.get(request.getModalitySiat()));
            }

            if (request.getActive() != null) {
                toUpdate.setActive(request.getActive());
            }

            if (request.getLogo() != null) {
                toUpdate.setLogo(request.getLogo());
            }

            companyRepository.save(toUpdate);
            companyRepository.flush();

            response.setCode(ResponseCodes.SUCCESS);
            response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
            response.setBody(new Object() {
                public final long id = toUpdate.getId();
            });
        } else {

            throw new NotFoundAlertException(
                ErrorKeys.ERR_RECORD_NOT_FOUND,
                ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO
            );
        }
        return response;
    }

    @Override
    public CrudRes get(Long companyId, Integer id) {
        return null;
    }

    public CrudRes delete(Long idCompany) {
        Optional<Company> fromDb = companyRepository.findById(idCompany);
        CrudRes response = new CrudRes();
        if (fromDb.isPresent()) {
            Company company = fromDb.get();
            companyRepository.delete(company);
            companyRepository.flush();

            response.setCode(ResponseCodes.SUCCESS);
            response.setMessage(ResponseMessages.SUCCESS_ELIMINACION_EXITOSA);
        } else {
            throw new NotFoundAlertException(
                ErrorKeys.ERR_RECORD_NOT_FOUND,
                ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO
            );
        }
        return response;
    }

    @Transactional
    public Company save(CompanyReq request) {
        Company entity = new Company();
        entity.setBusinessCode(GenUtil.businessCode());
        entity.setAddress(request.getAddress());
        entity.setBusinessName(request.getBusinessName());
        entity.setCity(request.getCity());
        entity.setEnvironmentSiat(request.getEnvironmentSiat());
        entity.setEventSend(request.getEventSend());
        entity.setModalitySiat(request.getModalitySiat());
        entity.setName(request.getName());
        entity.setNit(request.getNit());
        entity.setPackageSend(request.getPackageSend());
        entity.setEmailNotification(request.getEmailNotification());
        entity.setToken(request.getToken());
        entity.setPhone(request.getPhone());
        entity.setSystemCode(request.getSystemCode());
        entity.setActive(true);
        entity.setLogo(request.getLogo());
        entity = companyRepository.save(entity);
        log.info("Request to save Company : {}", entity);
        return entity;
    }

    @Transactional
    public Optional<Company> partialUpdate(CompanyReq company) {
        return companyRepository
            .findById(company.getId())
            .map(existingCompany -> {
                if (company.getEventSend() != null) {
                    existingCompany.setEventSend(company.getEventSend());
                }

                if (company.getPackageSend() != null) {
                    existingCompany.setPackageSend(company.getPackageSend());
                }

                if (company.getNit() != null) {
                    existingCompany.setNit(company.getNit());
                }

                if (company.getName() != null) {
                    existingCompany.setName(company.getName());
                }

                if (company.getBusinessCode() != null) {
                    existingCompany.setBusinessCode(company.getBusinessCode());
                }

                if (company.getBusinessName() != null) {
                    existingCompany.setBusinessName(company.getBusinessName());
                }

                if (company.getCity() != null) {
                    existingCompany.setCity(company.getCity());
                }

                if (company.getPhone() != null) {
                    existingCompany.setPhone(company.getPhone());
                }

                if (company.getAddress() != null) {
                    existingCompany.setAddress(company.getAddress());
                }

                if (company.getSystemCode() != null) {
                    existingCompany.setSystemCode(company.getSystemCode());
                }

                if (company.getEmailNotification() != null) {
                    existingCompany.setEmailNotification(company.getEmailNotification());
                }

                if (company.getToken() != null) {
                    existingCompany.setToken(company.getToken());
                }

                if (company.getEnvironmentSiat() != null) {
                    existingCompany.setEnvironmentSiat(company.getEnvironmentSiat());
                }

                if (company.getModalitySiat() != null) {
                    existingCompany.setModalitySiat(company.getModalitySiat());
                }

                if (company.getActive() != null) {
                    existingCompany.setActive(company.getActive());
                }

                if (company.getLogo() != null) {
                    existingCompany.setLogo(company.getLogo());
                }

                log.info("Request to partially update Company : {}", company);
                return existingCompany;
            });
    }

    public Page<Company> findAll(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    public Optional<Company> findOne(Long id) {
        return companyRepository.findById(id);
    }

    @Transactional
    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
        log.info("Request to delete Company : {}", id);
    }
}
