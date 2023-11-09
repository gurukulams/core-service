package com.gurukulams.core.service;

import com.gurukulams.core.model.LearnerProfile;
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

class LearnerProfileServiceTest {

    private final LearnerService learnerService;

    private final LearnerProfileService learnerProfileService;

    private final ProfileService profileService;

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
        learnerProfileService.delete();
        learnerService.delete();
        learnerService.signUp(LearnerServiceTest.aSignupRequest(),
                s -> String.valueOf(new StringBuilder(s).reverse()));
    }

    LearnerProfileServiceTest() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        this.learnerProfileService = new LearnerProfileService(TestUtil.gurukulamsManager(), validator);
        this.learnerService = new LearnerService(TestUtil.gurukulamsManager(), validator);
        this.profileService = new ProfileService(this.learnerService,this.learnerProfileService);
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

    private RegistrationRequest newRegistrationRequest() {
        RegistrationRequest newRegistrationRequest = new RegistrationRequest();
        newRegistrationRequest.setName("Sathish Kumar");
        newRegistrationRequest.setDob(LocalDate.now().minusYears(20L));
        return newRegistrationRequest;
    }
}
