package bo.com.luminia.sflbilling.msaccount.service.sfe;

import bo.com.luminia.sflbilling.msaccount.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msaccount.repository.SignatureRepository;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.Signature;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec.DefaultTransactionException;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec.RecordNotFoundException;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.SignatureCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.SignatureUpdateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.CrudRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.SignatureRes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class SignatureService implements BaseCrudService<SignatureCreateReq, SignatureUpdateReq, CrudRes> {

    private final SignatureRepository repository;
    private final CompanyRepository companyRepository;

    private CrudRes createResponse(Integer code, String message, Object body) {
        CrudRes response = new CrudRes();
        response.setCode(code);
        response.setMessage(message);
        response.setBody(body);
        return response;
    }

    @Override
    public CrudRes create(SignatureCreateReq request) {
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

                //return createResponse(ResponseCodes.ERROR, String.format(ResponseMessages.ERROR_EMPRESA_NO_ENCONTRADA, request.getCompanyId()), null);
            }

            Optional<Signature> fromdb = repository.findByCompanyIdAndActiveIsTrue(request.getCompanyId());
            if (fromdb.isPresent()) {
                throw new DefaultTransactionException(
                    ResponseMessages.ERROR_EXISTE_SIGNATURE,
                    ErrorEntities.COMPANY,
                    ErrorKeys.ERR_RECORD_ALREADY_EXISTS);

//                return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_EXISTE_SIGNATURE, null);
            }

            Signature entity = new Signature();

            entity.setCompany(companyFromDb.get());
            entity.setCertificate(request.getCertificate());
            entity.setPrivateKey(request.getPrivateKey());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate date = LocalDate.parse(request.getStartDate(), formatter);
            ZonedDateTime startDate = date.atStartOfDay(ZoneId.systemDefault());
            entity.setStartDate(startDate);

            LocalDate date2 = LocalDate.parse(request.getEndDate(), formatter);
            ZonedDateTime endDate = date2.atStartOfDay(ZoneId.systemDefault());
            entity.setEndDate(endDate);

            entity.setActive(true);

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

        if (companyId != null) {
            Optional<Signature> signature = repository.findByCompanyIdAndActiveIsTrue(companyId);
            if (!signature.isPresent()) {
                throw new RecordNotFoundException();
                //return
                //    createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }
            Signature entity = signature.get();
            entity.setActive(false);

            repository.save(entity);
            repository.flush();

            return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_ELIMINACION_EXITOSA, null);
        }

        throw new DefaultTransactionException(
            ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID,
            ErrorEntities.SIGNATURE,
            ErrorKeys.ERR_REQUIRED_VALUE);

        //return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID, null);
    }

    @Override
    public CrudRes update(SignatureUpdateReq request) {

        if (request.getId() != null) {
            Optional<Signature> result = repository.findByCompanyIdAndIdAndActiveIsTrue(request.getCompanyId(), request.getId());
            if (result.isPresent()) {
                Signature entity = result.get();
                if (request.getCertificate() != null) {
                    entity.setCertificate(request.getCertificate());
                }
                if (request.getPrivateKey() != null) {
                    entity.setPrivateKey(request.getPrivateKey());
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                if (request.getStartDate() != null) {
                    LocalDate date = LocalDate.parse(request.getStartDate(), formatter);
                    ZonedDateTime startDate = date.atStartOfDay(ZoneId.systemDefault());
                    entity.setStartDate(startDate);
                }

                if (request.getEndDate() != null) {
                    LocalDate date = LocalDate.parse(request.getEndDate(), formatter);
                    ZonedDateTime endDate = date.atStartOfDay(ZoneId.systemDefault());
                    entity.setEndDate(endDate);
                }

                if (request.getActive() != null) {
                    entity.setActive(request.getActive());
                }

                if (request.getCompanyId() != null) {
                    Optional<Company> companyOptional = companyRepository.findByIdAndActiveTrue(request.getCompanyId());
                    if (companyOptional.isPresent()) {

                    } else {
                        throw new DefaultTransactionException(
                            String.format(ResponseMessages.ERROR_REGISTRO_COMPANY_NO_ENCONTRADO, request.getCompanyId()),
                            ErrorEntities.COMPANY,
                            ErrorKeys.ERR_RECORD_NOT_FOUND);
                        //return createResponse(ResponseCodes.ERROR, String.format(ResponseMessages.ERROR_REGISTRO_COMPANY_NO_ENCONTRADO, request.getCompanyId()), null);
                    }
                }

                repository.save(entity);
                repository.flush();

                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_ACTUALIZACION_EXITOSA,
                    new Object() {
                        public final long id = entity.getId();
                    });
            } else {
                throw new RecordNotFoundException();
                //return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }
        } else {
            throw new DefaultTransactionException(
                ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID,
                ErrorEntities.SIGNATURE,
                ErrorKeys.ERR_REQUIRED_VALUE);

            //return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID, null);
        }

    }

    @Override
    public CrudRes get(Long companyId, Integer id) {
        return null;
    }

    public CrudRes get(Long id) {
        if (id != null) {
            Optional<Signature> entity = repository.findById(id);
            if (entity.isPresent()) {
                SignatureRes body = new SignatureRes();
                BeanUtils.copyProperties(entity.get(), body);

                body.setStartDate(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(entity.get().getStartDate()));
                body.setEndDate(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(entity.get().getEndDate()));
                body.setCompanyId(entity.get().getCompany().getId());
                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, body);
            } else {
                return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }
        } else {
            return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
        }
    }

    public CrudRes getByCompany(Long companyId) {
        if (companyId != null) {
            Optional<Signature> entity = repository.findByCompanyIdAndActiveIsTrue(companyId);
            if (entity.isPresent()) {
                SignatureRes body = new SignatureRes();
                BeanUtils.copyProperties(entity.get(), body);

                body.setStartDate(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(entity.get().getStartDate()));
                body.setEndDate(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(entity.get().getEndDate()));
                body.setCompanyId(entity.get().getCompany().getId());
                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, body);
            } else {
                return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }
        } else {
            return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
        }
    }
}
