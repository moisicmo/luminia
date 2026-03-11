package bo.com.luminia.sflbilling.msaccount.web.rest;

import bo.com.luminia.sflbilling.domain.BranchOffice;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.User;
import bo.com.luminia.sflbilling.msaccount.config.Constants;
import bo.com.luminia.sflbilling.msaccount.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msaccount.repository.UserRepository;
import bo.com.luminia.sflbilling.msaccount.service.UserService;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.NotFoundAlertException;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.UserCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.UserUpdateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.CrudRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.UserRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.util.PaginationUtil;
import bo.com.luminia.sflbilling.msaccount.web.rest.util.ResponseUtil;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '"+ AuthoritiesConstants.COMPANY_ADMIN +"')")
public class UserResource {

    private final UserRepository userRepository;

    private final UserService userService;

    private final CompanyRepository companyRepository;

    @PostMapping("/users")
    public ResponseEntity<CrudRes> create(@Valid @RequestBody UserCreateReq userCreateReq) throws URISyntaxException {
        log.debug("REST request to save User: {}", userCreateReq);
        CrudRes response = new CrudRes();
        response.setCode(ResponseCodes.WARNING);

        Company company = null;
        if (userCreateReq.getCompanyId() != null)
            company = companyRepository.findById(userCreateReq.getCompanyId()).orElseThrow(() -> new NotFoundAlertException("COMPANY_NOT_FOUND", "Company not found"));

        if (userRepository.findOneByLogin(userCreateReq.getLogin().toLowerCase()).isPresent()) {
            response.setMessage("Ya existe una cuenta con el nombre de usuario.");
        } else if (userRepository.findOneByEmailIgnoreCase(userCreateReq.getEmail()).isPresent()) {
            response.setMessage("Ya existe una cuenta con el correo electrónico.");
        } else {
            User entity = userService.create(userCreateReq, company);

            response.setCode(ResponseCodes.SUCCESS);
            response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
            response.setBody(new Object() {
                public final long id = entity.getId();
            });
        }
        return ResponseUtil.wrapOrNotFound(Optional.of(response));
    }

    @PutMapping("/users")
    public ResponseEntity<CrudRes> update(@Valid @RequestBody UserUpdateReq userUpdateReq) {
        log.debug("REST request to update User: {}", userUpdateReq);
        CrudRes response = new CrudRes();
        response.setCode(ResponseCodes.WARNING);

        Company company = null;
        if (userUpdateReq.getCompanyId() != null)
            company = companyRepository.findById(userUpdateReq.getCompanyId()).orElseThrow(() -> new NotFoundAlertException("COMPANY_NOT_FOUND", "Company not found"));

        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userUpdateReq.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userUpdateReq.getId()))) {
            response.setMessage("Ya existe una cuenta con el correo electrónico.");
        } else {
            Optional<UserRes> updatedUser = userService.update(userUpdateReq, company);
            if (!updatedUser.isPresent()) {
                response.setCode(ResponseCodes.ERROR);
                response.setMessage(ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO);
            } else {
                response.setCode(ResponseCodes.SUCCESS);
                response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
                response.setBody(new Object() {
                    public final long id = updatedUser.get().getId();
                });
            }
        }
        return ResponseUtil.wrapOrNotFound(Optional.of(response));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<CrudRes> delete(@PathVariable Long id) {
        log.debug("REST request to delete User: {}", id);
        CrudRes response = new CrudRes();
        response.setCode(ResponseCodes.WARNING);

        Optional<User> entity = userRepository.findById(id);
        if (!entity.isPresent()) {
            response.setCode(ResponseCodes.ERROR);
            response.setMessage(ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO);
        } else {
            userService.delete(id);
            response.setCode(ResponseCodes.SUCCESS);
            response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
        }
        return ResponseUtil.wrapOrNotFound(Optional.of(response));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<CrudRes> get(@PathVariable Long id) {
        log.debug("REST request to get User : {}", id);
        CrudRes response = new CrudRes();
        response.setCode(ResponseCodes.WARNING);

        Optional<User> entity = userRepository.findById(id);
        if (!entity.isPresent()) {
            response.setCode(ResponseCodes.ERROR);
            response.setMessage(ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO);
        } else {
            response.setCode(ResponseCodes.SUCCESS);
            response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
            response.setBody(new UserRes(entity.get()));
        }
        return ResponseUtil.wrapOrNotFound(Optional.of(response));
    }

    @GetMapping("/users/login/{login}")
    public ResponseEntity<UserRes> getUser(@PathVariable @Pattern(regexp = Constants.LOGIN_REGEX) String login) {
        log.debug("REST request to get User: {}", login);
        Optional<UserRes> result = userRepository.findOneWithAuthoritiesByLogin(login).map(UserRes::new);
        return ResponseUtil.wrapOrNotFound(result);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserRes>> getAllUsers(Pageable pageable) {
        log.debug("REST request to get all User for an admin");
        final Page<UserRes> page = userRepository.findAll(pageable).map(UserRes::new);
        HttpHeaders headers = PaginationUtil.addTotalCountHttpHeaders(page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
