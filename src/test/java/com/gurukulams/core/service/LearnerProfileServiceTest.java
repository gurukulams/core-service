package com.gurukulams.core.service;

import com.gurukulams.core.model.LearnerProfile;
import com.gurukulams.core.payload.RegistrationRequest;
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
    }

    @Test
    void testInvalidCreate() {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setDob(LocalDate.now().plusYears(20L));
        registrationRequest.setName("Hello");

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            learnerProfileService.create(learnerService
                            .readByEmail(LearnerServiceTest.aSignupRequest().getEmail())
                            .get().userHandle(),
                    registrationRequest);
        });
    }

    @Test
    void create() throws SQLException {
        final LearnerProfile org = learnerProfileService.create(learnerService
                        .readByEmail(LearnerServiceTest.aSignupRequest().getEmail())
                        .get().userHandle(),
                newRegistrationRequest());
        Assertions.assertTrue(learnerProfileService.read(org.getUserHandle()).isPresent(), "Created org");
    }

    private RegistrationRequest newRegistrationRequest() {
        RegistrationRequest newRegistrationRequest = new RegistrationRequest();
        newRegistrationRequest.setDob(LocalDate.now().minusYears(20L));
        newRegistrationRequest.setName("Hello");
        return newRegistrationRequest;
    }
}
