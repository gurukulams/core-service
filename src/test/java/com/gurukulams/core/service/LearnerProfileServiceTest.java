package com.gurukulams.core.service;

import com.gurukulams.core.model.LearnerProfile;
import com.gurukulams.core.model.Org;
import com.gurukulams.core.payload.Learner;
import com.gurukulams.core.payload.RegistrationRequest;
import com.gurukulams.core.payload.SignupRequest;
import com.gurukulams.core.util.TestUtil;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

class LearnerProfileServiceTest {

    private final LearnerService learnerService;

    private final LearnerProfileService learnerProfileService;

    private final ProfileService profileService;

    private final OrgService orgService;

    /**
     * Before.
     *
     * @throws SQLException the io exception
     */
    @BeforeEach
    void before() throws SQLException {
        cleanUp();
    }

    /**
     * After.
     */
    @AfterEach
    void after() throws SQLException {
        cleanUp();
    }

    private void cleanUp() throws SQLException {
        orgService.delete();
        learnerProfileService.delete();
        learnerService.delete();
        Assertions.assertEquals(0, profileService.list("USER", null).size());
        learnerService.signUp(LearnerServiceTest.aSignupRequest(),
                s -> String.valueOf(new StringBuilder(s).reverse()));
    }

    LearnerProfileServiceTest() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        this.learnerProfileService = new LearnerProfileService(TestUtil.gurukulamsManager(), validator);
        this.learnerService = new LearnerService(TestUtil.gurukulamsManager(), validator);
        this.profileService = new ProfileService(TestUtil.gurukulamsManager(),this.learnerService,this.learnerProfileService,
                new OrgService(TestUtil.gurukulamsManager()));
        this.orgService = new OrgService(TestUtil.gurukulamsManager());
    }

    @Test
    void testInvalidCreate() {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setName("Hello");

        // Future DOB ?! - Not Valid
        registrationRequest.setDob(LocalDate.now().plusYears(20L));
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            learnerProfileService.create(learnerService
                            .readByEmail(LearnerServiceTest.aSignupRequest().getEmail())
                            .get().userHandle(),
                    registrationRequest);
        });

        // More ihan 80 Years ?! - Not Valid
        registrationRequest.setDob(LocalDate.now().minusYears(81L));
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            learnerProfileService.create(learnerService
                            .readByEmail(LearnerServiceTest.aSignupRequest().getEmail())
                            .get().userHandle(),
                    registrationRequest);
        });

        // Less than 10 Years ?! - Not Valid
        registrationRequest.setDob(LocalDate.now().minusYears(9L));
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            learnerProfileService.create(learnerService
                            .readByEmail(LearnerServiceTest.aSignupRequest().getEmail())
                            .get().userHandle(),
                    registrationRequest);
        });

        // Name with Numbers ? - Not Valid
        registrationRequest.setName("hjghs565");
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            learnerProfileService.create(learnerService
                            .readByEmail(LearnerServiceTest.aSignupRequest().getEmail())
                            .get().userHandle(),
                    registrationRequest);
        });

        // Name with Spl Chars ? - Not Valid
        registrationRequest.setName("hell@#$");
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            learnerProfileService.create(learnerService
                            .readByEmail(LearnerServiceTest.aSignupRequest().getEmail())
                            .get().userHandle(),
                    registrationRequest);
        });


    }

    @Test
    void create() throws SQLException {
        SignupRequest signupRequest = LearnerServiceTest.aSignupRequest();
        Assertions.assertFalse(profileService.read(this.learnerService.readByEmail(signupRequest.getEmail()).get().userHandle()).isPresent());
        final LearnerProfile learnerProfile = learnerProfileService.create(learnerService
                        .readByEmail(signupRequest.getEmail())
                        .get().userHandle(),
                newRegistrationRequest());
        Assertions.assertTrue(learnerProfileService.read(learnerProfile.getUserHandle()).isPresent(), "Created learnerProfile");
        Assertions.assertTrue(profileService.read(learnerProfile.getUserHandle()).isPresent());
    }

    @Test
    void list() throws SQLException {
        SignupRequest signupRequest = LearnerServiceTest.aSignupRequest();
        Assertions.assertFalse(profileService.read(this.learnerService.readByEmail(signupRequest.getEmail()).get().userHandle()).isPresent());
        final LearnerProfile learnerProfile = learnerProfileService.create(learnerService
                        .readByEmail(signupRequest.getEmail())
                        .get().userHandle(),
                newRegistrationRequest());
        Assertions.assertTrue(learnerProfileService.read(learnerProfile.getUserHandle()).isPresent(), "Created learnerProfile");

        final Org org = orgService.create("hari", Locale.GERMAN,
                OrgServiceTest.anOrg());

        Assertions.assertEquals(2, profileService.list(learnerProfile.getUserHandle(), null).size());
    }

    @Test
    void register() throws SQLException {
        SignupRequest signupRequest = LearnerServiceTest.aSignupRequest();

        final Learner learner = learnerService.readByEmail(signupRequest.getEmail())
                .get();

        final LearnerProfile learnerProfile = learnerProfileService.create(learnerService
                        .readByEmail(signupRequest.getEmail())
                        .get().userHandle(),
                newRegistrationRequest());

        signupRequest.setEmail("EMAIL@email.com");
        learnerService.signUp(signupRequest,
                s -> String.valueOf(new StringBuilder(s).reverse()));
        final Learner learner2 = learnerService.readByEmail(signupRequest.getEmail())
                .get();
        final LearnerProfile learnerProfile2 = learnerProfileService.create(learnerService
                        .readByEmail(signupRequest.getEmail())
                        .get().userHandle(),
                newRegistrationRequest());

        Assertions.assertTrue(profileService.register(learner.userHandle(), learner2.userHandle()));

        Assertions.assertTrue(profileService.isRegistered(learner.userHandle(), learner2.userHandle()));

        Assertions.assertEquals(1, profileService.getBuddies(learner.userHandle()).size());

        // registering again ? - Invalid
        Assertions.assertThrows(SQLException.class, () -> {
            profileService.register(learner.userHandle(), learner2.userHandle());
        });

        // registering for invalid event ? - Invalid
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            profileService.register(learner.userHandle(), UUID.randomUUID().toString());
        });

        signupRequest.setEmail("EMAIL3@email.com");
        learnerService.signUp(signupRequest,
                s -> String.valueOf(new StringBuilder(s).reverse()));
        final Learner learner3 = learnerService.readByEmail(signupRequest.getEmail())
                .get();
        final LearnerProfile learnerProfile3 = learnerProfileService.create(learnerService
                        .readByEmail(signupRequest.getEmail())
                        .get().userHandle(),
                newRegistrationRequest());

        Assertions.assertTrue(profileService.register(learner.userHandle(), learner3.userHandle()));

        Assertions.assertEquals(2, profileService.getBuddies(learner.userHandle()).size());
    }

    private RegistrationRequest newRegistrationRequest() {
        RegistrationRequest newRegistrationRequest = new RegistrationRequest();
        newRegistrationRequest.setName("Sathish Kumar");
        newRegistrationRequest.setDob(LocalDate.now().minusYears(20L));
        return newRegistrationRequest;
    }
}
