package bo.com.luminia.sflbilling.msaccount.web.rest;

import bo.com.luminia.sflbilling.domain.User;
import bo.com.luminia.sflbilling.msaccount.IntegrationTest;
import bo.com.luminia.sflbilling.msaccount.repository.UserRepository;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.UserCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.UserUpdateReq;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@IntegrationTest
class UserResourceIT {

    private static final Logger log = LoggerFactory.getLogger(UserResourceIT.class);

    private static final String DEFAULT_LOGIN = "johndoe";
    private static final String UPDATED_LOGIN = "jhipster";

    private static final Long DEFAULT_ID = 1L;

    private static final String DEFAULT_PASSWORD = "passjohndoe";
    private static final String UPDATED_PASSWORD = "passjhipster";

    private static final String DEFAULT_EMAIL = "johndoe@localhost";
    private static final String UPDATED_EMAIL = "jhipster@localhost";

    private static final String DEFAULT_FIRSTNAME = "john";
    private static final String UPDATED_FIRSTNAME = "jhipsterFirstName";

    private static final String DEFAULT_LASTNAME = "doe";
    private static final String UPDATED_LASTNAME = "jhipsterLastName";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc restUserMockMvc;

    private User user;

    @BeforeEach
    public void setup() {
    }

    public static User createEntity() {
        User user = new User();
        user.setLogin(DEFAULT_LOGIN + RandomStringUtils.randomAlphabetic(5));
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail(RandomStringUtils.randomAlphabetic(5) + DEFAULT_EMAIL);
        user.setFirstName(DEFAULT_FIRSTNAME);
        user.setLastName(DEFAULT_LASTNAME);
        return user;
    }

    /**
     * Setups the database with one user.
     */
    public static User initTestUser() {
        User user = createEntity();
        user.setLogin(DEFAULT_LOGIN);
        user.setEmail(DEFAULT_EMAIL);
        return user;
    }

    @BeforeEach
    public void initTest() {
        user = initTestUser();
    }

    @Test
    @Transactional
    void createUser() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        // Create the User
        UserCreateReq userCreateReq = new UserCreateReq();
        userCreateReq.setLogin(DEFAULT_LOGIN);
        userCreateReq.setPassword(DEFAULT_PASSWORD);
        userCreateReq.setFirstName(DEFAULT_FIRSTNAME);
        userCreateReq.setLastName(DEFAULT_LASTNAME);
        userCreateReq.setEmail(DEFAULT_EMAIL);
        userCreateReq.setAuthorities(Collections.singleton(AuthoritiesConstants.ADMIN));

        restUserMockMvc
            .perform(
                post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userCreateReq))
            )
            .andExpect(status().isCreated());

        // Validate the User in the database
        assertPersistedUsers(
            users -> {
                assertThat(users).hasSize(databaseSizeBeforeCreate + 1);
                User testUser = users.get(users.size() - 1);
                assertThat(testUser.getLogin()).isEqualTo(DEFAULT_LOGIN);
                assertThat(testUser.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
                assertThat(testUser.getLastName()).isEqualTo(DEFAULT_LASTNAME);
                assertThat(testUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
            }
        );
    }

    @Test
    @Transactional
    void createUserWithExistingLogin() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        UserCreateReq userCreateReq = new UserCreateReq();
        userCreateReq.setLogin(DEFAULT_LOGIN); // this login should already be used
        userCreateReq.setPassword(DEFAULT_PASSWORD);
        userCreateReq.setFirstName(DEFAULT_FIRSTNAME);
        userCreateReq.setLastName(DEFAULT_LASTNAME);
        userCreateReq.setEmail("anothermail@localhost");
        userCreateReq.setAuthorities(Collections.singleton(AuthoritiesConstants.ADMIN));

        // Create the User
        restUserMockMvc
            .perform(
                post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userCreateReq))
            )
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
    }

    @Test
    @Transactional
    void createUserWithExistingEmail() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        UserCreateReq managedUserVM = new UserCreateReq();
        managedUserVM.setLogin("anotherlogin");
        managedUserVM.setPassword(DEFAULT_PASSWORD);
        managedUserVM.setFirstName(DEFAULT_FIRSTNAME);
        managedUserVM.setLastName(DEFAULT_LASTNAME);
        managedUserVM.setEmail(DEFAULT_EMAIL); // this email should already be used
        managedUserVM.setAuthorities(Collections.singleton(AuthoritiesConstants.ADMIN));

        // Create the User
        restUserMockMvc
            .perform(
                post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(managedUserVM))
            )
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
    }

    @Test
    @Transactional
    void getAllUsers() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        // Get all the users
        restUserMockMvc
            .perform(get("/api/admin/users?sort=id,desc").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRSTNAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LASTNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
    }

    @Test
    @Transactional
    void getUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        // Get the user
        restUserMockMvc
            .perform(get("/api/admin/users/{login}", user.getLogin()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.login").value(user.getLogin()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRSTNAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LASTNAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
    }

    @Test
    @Transactional
    void getNonExistingUser() throws Exception {
        restUserMockMvc.perform(get("/api/admin/users/unknown")).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeUpdate = userRepository.findAll().size();

        // Update the user
        User updatedUser = userRepository.findById(user.getId()).get();

        UserUpdateReq userUpdateReq = new UserUpdateReq();
        userUpdateReq.setId(updatedUser.getId());
        userUpdateReq.setFirstName(UPDATED_FIRSTNAME);
        userUpdateReq.setLastName(UPDATED_LASTNAME);
        userUpdateReq.setEmail(UPDATED_EMAIL);
        userUpdateReq.setActivated(updatedUser.isActivated());
        userUpdateReq.setAuthorities(Collections.singleton(AuthoritiesConstants.ADMIN));

        restUserMockMvc
            .perform(
                put("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userUpdateReq))
            )
            .andExpect(status().isOk());

        // Validate the User in the database
        assertPersistedUsers(
            users -> {
                assertThat(users).hasSize(databaseSizeBeforeUpdate);
                User testUser = users.stream().filter(usr -> usr.getId().equals(updatedUser.getId())).findFirst().get();
                assertThat(testUser.getFirstName()).isEqualTo(UPDATED_FIRSTNAME);
                assertThat(testUser.getLastName()).isEqualTo(UPDATED_LASTNAME);
                assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
            }
        );
    }

    @Test
    @Transactional
    void deleteUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeDelete = userRepository.findAll().size();

        // Delete the user
        restUserMockMvc
            .perform(delete("/api/admin/users/{login}", user.getLogin()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeDelete - 1));
    }

    private void assertPersistedUsers(Consumer<List<User>> userAssertion) {
        userAssertion.accept(userRepository.findAll());
    }
}
