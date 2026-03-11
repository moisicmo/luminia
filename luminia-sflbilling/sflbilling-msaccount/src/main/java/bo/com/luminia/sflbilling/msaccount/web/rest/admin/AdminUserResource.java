package bo.com.luminia.sflbilling.msaccount.web.rest.admin;

import bo.com.luminia.sflbilling.msaccount.config.Constants;
import bo.com.luminia.sflbilling.msaccount.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msaccount.repository.UserRepository;
import bo.com.luminia.sflbilling.msaccount.service.UserService;
import bo.com.luminia.sflbilling.msaccount.service.exceptions.EmailAlreadyUsedException;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec.LoginAlreadyUsedException;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.UserCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.UserUpdateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.UserRes;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.User;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.NotFoundAlertException;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_ADMIN + "')")
public class AdminUserResource {

    private final UserService userService;

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    @PostMapping("/users")
    public ResponseEntity<UserRes> createUser(@Valid @RequestBody UserCreateReq request) throws URISyntaxException {
        log.debug("REST request to save User: {}", request);
        if (userRepository.findOneByLogin(request.getLogin().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException();
        } else if (userRepository.findOneByEmailIgnoreCase(request.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        }
        Company company = companyRepository.findById(request.getCompanyId()).orElseThrow(() -> new NotFoundAlertException(ErrorKeys.ERR_RECORD_NOT_FOUND,
            ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO));
        User result = userService.create(request, company);
        return ResponseEntity
            .created(new URI("/api/admin/users/" + result.getLogin()))
            .body(new UserRes(result));
    }

    @PutMapping("/users")
    public ResponseEntity<UserRes> updateUser(@Valid @RequestBody UserUpdateReq request) {
        log.debug("REST request to update User: {}", request);
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(request.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(request.getId()))) {
            throw new EmailAlreadyUsedException();
        }
        Company company = companyRepository.findById(request.getCompanyId()).orElseThrow(() -> new NotFoundAlertException(ErrorKeys.ERR_RECORD_NOT_FOUND,
            ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO));
        Optional<UserRes> result = userService.partialUpdateFromRequest(request, company).map(UserRes::new);
        return ResponseUtil.wrapOrNotFound(result);
    }

    @DeleteMapping("/users/{login}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Pattern(regexp = Constants.LOGIN_REGEX) String login) {
        log.debug("REST request to delete User: {}", login);
        if (!userRepository.existsByLogin(login)) throw new NotFoundAlertException(ErrorKeys.ERR_RECORD_NOT_FOUND,
            ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO);
        userService.deleteUser(login);
        return ResponseEntity
            .noContent()
            .build();
    }

    @GetMapping("/users/{login}")
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
