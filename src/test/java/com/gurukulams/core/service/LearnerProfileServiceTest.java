package com.gurukulams.core.service;

import com.gurukulams.core.model.LearnerProfile;
import com.gurukulams.core.util.TestUtil;
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
    }

    LearnerProfileServiceTest() {
        this.learnerProfileService = new LearnerProfileService(TestUtil.gurukulamsManager());
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

        Validator validator = validatorFactory.getValidator();
        this.learnerService = new LearnerService(TestUtil.gurukulamsManager(), validator);
    }

    @Test
    void create() throws SQLException {
        final LearnerProfile org = learnerProfileService.create(newLearnerProfile());
        Assertions.assertTrue(learnerProfileService.read(org.getUserHandle()).isPresent(), "Created org");
    }

    private LearnerProfile newLearnerProfile() throws SQLException {

        learnerService.signUp(LearnerServiceTest.aSignupRequest(),
                s -> String.valueOf(new StringBuilder(s).reverse()));



        LearnerProfile learnerProfile = new LearnerProfile();
        learnerProfile.setDob(LocalDate.now().minusYears(20L));
        learnerProfile.setName("Hello");
        learnerProfile.setUserHandle(learnerService.readByEmail(LearnerServiceTest.aSignupRequest().getEmail()).get().userHandle());
        return learnerProfile;
    }
}