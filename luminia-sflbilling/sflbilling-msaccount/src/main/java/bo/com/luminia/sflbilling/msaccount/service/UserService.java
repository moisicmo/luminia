package bo.com.luminia.sflbilling.msaccount.service;

import bo.com.luminia.sflbilling.msaccount.repository.AuthorityRepository;
import bo.com.luminia.sflbilling.msaccount.repository.UserRepository;
import bo.com.luminia.sflbilling.msaccount.security.SecurityUtils;
import bo.com.luminia.sflbilling.domain.Authority;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.User;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.UserCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.UserUpdateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.UserRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User create(UserCreateReq request, Company company) {
        User entity = new User();
        entity.setLogin(request.getLogin());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setActivated(true);
        entity.setCompany(company);
        if (request.getEmail() != null) entity.setEmail(request.getEmail().toLowerCase());
        if (request.getAuthorities() != null)
            entity.setAuthorities(request.getAuthorities().stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet()));
        userRepository.save(entity);
        log.info("Created Information for User: {}", entity);
        return entity;
    }

    @Transactional
    public Optional<UserRes> update(UserUpdateReq request, Company company) {
        return Optional
            .of(userRepository.findById(request.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    if (request.getEmail() != null) {
                        user.setEmail(request.getEmail().toLowerCase());
                    }
                    user.setActivated(request.getActivated());
                    user.setCompany(company);
                    Set<Authority> managedAuthorities = user.getAuthorities();
                    managedAuthorities.clear();
                    request
                        .getAuthorities()
                        .stream()
                        .map(authorityRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(managedAuthorities::add);
                    log.info("Changed Information for User: {}", user);
                    return user;
                }
            )
            .map(UserRes::new);
    }

    public void delete(Long id) {
        userRepository
            .findById(id)
            .ifPresent(
                user -> {
                    userRepository.delete(user);
                    log.info("Deleted User: {}", user);
                }
            );
    }

    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    public void deleteUser(String login) {
        userRepository.findOneByLogin(login)
            .ifPresent(user -> {
                    userRepository.delete(user);
                    log.info("User deleted: {}", user);
                }
            );
    }

    @Transactional
    public Optional<User> partialUpdateFromRequest(UserUpdateReq request, Company company) {
        return userRepository
            .findById(request.getId())
            .map(existingUser -> {
                if (request.getFirstName() != null) {
                    existingUser.setFirstName(request.getFirstName());
                }
                if (request.getLastName() != null) {
                    existingUser.setLastName(request.getLastName());
                }
                if (request.getEmail() != null) {
                    existingUser.setEmail(request.getEmail());
                }
                if (request.getActivated() != null) {
                    existingUser.setActivated(request.getActivated());
                }
                if (request.getCompanyId() != null && company != null) {
                    existingUser.setCompany(company);
                }
                if (request.getAuthorities() != null && !request.getAuthorities().isEmpty()) {
                    existingUser.setAuthorities(request.getAuthorities().stream()
                        .map(authorityRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet()));
                }

                log.info("User updated: {}", existingUser);
                return existingUser;
            });
    }
}
