package bo.com.luminia.sflbilling.msaccount.service.sfe;

import bo.com.luminia.sflbilling.msaccount.repository.BranchOfficeRepository;
import bo.com.luminia.sflbilling.msaccount.repository.CompanyRepository;
import bo.com.luminia.sflbilling.domain.BranchOffice;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec.DefaultTransactionException;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.admin.BranchOfficeReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.BranchOfficeCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.BranchOfficeUpdateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.CrudRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.BranchOfficeDomainRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.BranchOfficeRes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class BranchOfficeService implements BaseCrudService<BranchOfficeCreateReq, BranchOfficeUpdateReq, CrudRes> {

    private final BranchOfficeRepository repository;
    private final CompanyRepository companyRepository;

    private CrudRes createResponse(Integer code, String message, Object body) {
        CrudRes response = new CrudRes();
        response.setCode(code);
        response.setMessage(message);
        response.setBody(body);
        return response;
    }

    @Override
    public CrudRes create(BranchOfficeCreateReq request) {
        CrudRes response = new CrudRes();
        response.setCode(ResponseCodes.WARNING);
        response.setMessage(ResponseMessages.WARNING_OPERACION_NO_EJECUTADA);

        if (request.getCompanyId() != null) {
            Optional<Company> companyFromDb = companyRepository.findByIdAndActiveTrue(request.getCompanyId());
            if (!companyFromDb.isPresent()) {
                throw new DefaultTransactionException(
                    String.format(ResponseMessages.ERROR_EMPRESA_NO_ENCONTRADA, request.getCompanyId()),
                    ErrorEntities.COMPANY,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);
                //return createResponse(ResponseCodes.ERROR,
                // String.format(ResponseMessages.ERROR_EMPRESA_NO_ENCONTRADA, request.getCompanyId()), null);
            }

            List<BranchOffice> branches = repository.findByCompanyIdAndBranchOfficeSiatId(request.getCompanyId(), request.getBranchOfficeSiatId());

            if (!branches.isEmpty()) {
                throw new DefaultTransactionException(
                    String.format(ResponseMessages.ERROR_EXISTE_BRANCH_ID, request.getBranchOfficeSiatId()),
                    ErrorEntities.BRANCH_OFFICE,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);
                //return createResponse(ResponseCodes.ERROR,
                //    String.format(ResponseMessages.ERROR_EXISTE_BRANCH_ID, request.getBranchOfficeSiatId()), null);
            }

            BranchOffice entity = new BranchOffice();

            entity.setCompany(companyFromDb.get());
            entity.setName(request.getName());
            entity.setBranchOfficeSiatId(request.getBranchOfficeSiatId());
            entity.setDescription(request.getDescription());
            entity.setActive(request.getActive() != null ? request.getActive() : true);

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

    @Override
    public CrudRes delete(Long companyId, Integer id) {

        if (id != null && companyId != null) {
            Optional<BranchOffice> branchOffice = repository.findByCompanyIdAndBranchOfficeSiatIdAndActiveIsTrue(companyId, id);
            if (!branchOffice.isPresent()) {
                throw new DefaultTransactionException(
                    ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO,
                    ErrorEntities.COMPANY,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);

                //return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }

            BranchOffice entity = branchOffice.get();
            entity.setActive(false);

            repository.save(entity);
            repository.flush();

            return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_ELIMINACION_EXITOSA, null);
        }
        return null;
    }

    @Override
    public CrudRes update(BranchOfficeUpdateReq request) {
        CrudRes response = new CrudRes();

        if (request.getId() != null) {
            Optional<BranchOffice> result = repository.findById(request.getId());
            if (result.isPresent()) {
                BranchOffice entity = result.get();
                if (request.getDescription() != null) {
                    entity.setDescription(request.getDescription());
                }
                if (request.getName() != null) {
                    entity.setName(request.getName());
                }

                if (request.getBranchOfficeSiatId() != null) {
                    entity.setBranchOfficeSiatId(request.getBranchOfficeSiatId());
                }

                if (request.getActive() != null) {
                    entity.setActive(request.getActive());
                }

                if (request.getCompanyId() != null) {
                    Optional<Company> companyOptional = companyRepository.findByIdAndActiveTrue(request.getCompanyId());
                    if (companyOptional.isPresent()) {

                    } else {
                        response = createResponse(ResponseCodes.ERROR, String.format(ResponseMessages.ERROR_REGISTRO_COMPANY_NO_ENCONTRADO, request.getCompanyId()), null);
                        throw new DefaultTransactionException(
                            String.format(ResponseMessages.ERROR_REGISTRO_COMPANY_NO_ENCONTRADO, request.getCompanyId()),
                            ErrorEntities.COMPANY,
                            ErrorKeys.ERR_RECORD_NOT_FOUND);
                    }
                }

                repository.save(entity);
                repository.flush();

                response = createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_ACTUALIZACION_EXITOSA,
                    new Object() {
                        public final long id = entity.getId();
                    });
            } else {

                //response = createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
                throw new DefaultTransactionException(
                    ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO,
                    ErrorEntities.COMPANY,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);
            }
        } else {
            //response = createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID, null);
            throw new DefaultTransactionException(
                ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID,
                ErrorEntities.COMPANY,
                ErrorKeys.ERR_REQUIRED_VALUE);
        }

        return response;
    }

    @Override
    public CrudRes get(Long companyId, Integer id) {
        return null;
    }

    public CrudRes get(Long id) {
        if (id != null) {
            Optional<BranchOffice> entity = repository.findById(id);
            if (entity.isPresent()) {
                BranchOfficeRes body = new BranchOfficeRes();
                BeanUtils.copyProperties(entity.get(), body);
                body.setCompanyId(entity.get().getCompany().getId());
                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, body);
            } else {
                throw new DefaultTransactionException(
                    ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO,
                    ErrorEntities.BRANCH_OFFICE,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);

                //return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }
        } else {
            //return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            throw new DefaultTransactionException(
                ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO,
                ErrorEntities.BRANCH_OFFICE,
                ErrorKeys.ERR_RECORD_NOT_FOUND);
        }
    }

    public CrudRes getByCompany(Long companyId) {
        if (companyId != null) {
            List<BranchOffice> entity = repository.findByCompanyId(companyId);
            List<BranchOfficeRes> body = new ArrayList<>();
            if (!entity.isEmpty()) {
                for (BranchOffice branch : entity) {
                    BranchOfficeRes item = new BranchOfficeRes();
                    BeanUtils.copyProperties(branch, item);
                    item.setCompanyId(branch.getCompany().getId());
                    body.add(item);
                }
                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, body);
            } else {
                throw new DefaultTransactionException(
                    ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO,
                    ErrorEntities.BRANCH_OFFICE,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);

                //return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }
        } else {
            throw new DefaultTransactionException(
                ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO,
                ErrorEntities.COMPANY,
                ErrorKeys.ERR_RECORD_NOT_FOUND);

            //return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
        }
    }

    public CrudRes getByCompanyBusinessCode(String businessCode) {
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (company.isPresent()) {
            List<BranchOffice> entity = repository.findByCompanyId(company.get().getId());
            List<BranchOfficeDomainRes> body = new ArrayList<>();
            if (!entity.isEmpty()) {
                for (BranchOffice branch : entity) {
                    BranchOfficeDomainRes item = new BranchOfficeDomainRes();
                    BeanUtils.copyProperties(branch, item);
                    body.add(item);
                }
                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, body);
            } else {
                throw new DefaultTransactionException(
                    ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO,
                    ErrorEntities.BRANCH_OFFICE,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);

                //return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }
        } else {
            throw new DefaultTransactionException(
                ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO,
                ErrorEntities.COMPANY,
                ErrorKeys.ERR_RECORD_NOT_FOUND);

            //return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
        }
    }

    public CrudRes getAll() {
        List<BranchOffice> entity = repository.findAll();
        List<BranchOfficeRes> body = new ArrayList<>();
        if (!entity.isEmpty()) {
            for (BranchOffice branch : entity) {
                BranchOfficeRes item = new BranchOfficeRes();
                BeanUtils.copyProperties(branch, item);
                item.setCompanyId(branch.getCompany().getId());
                body.add(item);
            }
            return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, body);
        } else {
            throw new DefaultTransactionException(
                ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO,
                ErrorEntities.BRANCH_OFFICE,
                ErrorKeys.ERR_RECORD_NOT_FOUND);
        }
    }

    @Transactional
    public BranchOfficeReq save(BranchOfficeReq request, Company company) {
        BranchOffice branchOffice = this.toEntity(request, company);
        branchOffice = repository.save(branchOffice);
        return this.toDto(branchOffice);
    }

    @Transactional
    public Optional<BranchOfficeReq> partialUpdate(BranchOfficeReq request, Company company) {
        return repository
            .findById(request.getId())
            .map(entity -> {
                if (request.getDescription() != null) {
                    entity.setDescription(request.getDescription());
                }
                if (request.getName() != null) {
                    entity.setName(request.getName());
                }

                if (request.getBranchOfficeSiatId() != null) {
                    entity.setBranchOfficeSiatId(request.getBranchOfficeSiatId());
                }

                if (request.getActive() != null) {
                    entity.setActive(request.getActive());
                }

                if (request.getCompanyId() != null) {
                    entity.setCompany(company);
                }
                log.debug("Request to partially update BranchOffice : {}", request);
                return entity;
            })
            .map(repository::save)
            .map(this::toDto);
    }

    public Page<BranchOfficeReq> findAll(Pageable pageable) {
        log.debug("Request to get all BranchOffice");
        return repository.findAll(pageable).map(this::toDto);
    }

    public Optional<BranchOfficeReq> findOne(Long id) {
        log.debug("Request to get BranchOffice : {}", id);
        return repository.findById(id).map(this::toDto);
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete BranchOffice : {}", id);
        repository.deleteById(id);
    }

    public BranchOffice toEntity(BranchOfficeReq dto, Company company) {
        if (dto == null) {
            return null;
        }

        BranchOffice entity = new BranchOffice();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setBranchOfficeSiatId(dto.getBranchOfficeSiatId());
        entity.setDescription(dto.getDescription());
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);
        entity.setCompany(company);

        return entity;
    }

    public BranchOfficeReq toDto(BranchOffice dto) {
        if (dto == null) {
            return null;
        }

        BranchOfficeReq entity = new BranchOfficeReq();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setBranchOfficeSiatId(dto.getBranchOfficeSiatId());
        entity.setDescription(dto.getDescription());
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);
        entity.setCompanyId(dto.getCompany().getId());

        return entity;
    }
}
