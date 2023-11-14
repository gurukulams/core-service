package com.gurukulams.core.service;

import com.gurukulams.core.payload.Learner;
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


class LearnerServiceTest {
    private static final String HANDLE = "tom";
    private static final String EMAIL = HANDLE + "@gmail.com";

    private final LearnerService learnerService;

    LearnerServiceTest() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

        Validator validator = validatorFactory.getValidator();
        this.learnerService = new LearnerService(TestUtil.gurukulamsManager(), validator);
    }

    @BeforeEach
    void before() throws SQLException {
        cleanup();
    }

    @AfterEach
    void after() throws SQLException {
        cleanup();
    }

    private void cleanup() throws SQLException {
        learnerService.delete();
    }

    @Test
    void testSignUp() throws SQLException {
        learnerService.signUp(aSignupRequest(),
                s -> String.valueOf(new StringBuilder(s).reverse()));

        Assertions.assertTrue(learnerService.read(HANDLE).isPresent());
        Assertions.assertTrue(learnerService.readByEmail(EMAIL).isPresent());
    }

    @Test
    void testEmptyReads() throws SQLException {
        Assertions.assertFalse(learnerService.read(HANDLE).isPresent());
        Assertions.assertFalse(learnerService.readByEmail(EMAIL).isPresent());
    }

    @Test
    void testInvalidSignUp() {
        SignupRequest signupRequest = aSignupRequest();

        signupRequest.setEmail("Invalid Email");

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            learnerService.signUp(signupRequest,
                    s -> String.valueOf(new StringBuilder(s).reverse()));
        });

    }

    @Test
    void testUpdate() throws SQLException {
        learnerService.signUp(aSignupRequest(),
                s -> String.valueOf(new StringBuilder(s).reverse()));

        Learner learner = getLearnerWithNewPassword(
                learnerService.readByEmail(EMAIL).get());

        learnerService.update(HANDLE, learner);

        Assertions.assertEquals(learner.password(),
                learnerService.readByEmail(EMAIL).get().password());


    }

    @Test
    void testInvalidUpdate() throws SQLException {
        learnerService.signUp(aSignupRequest(),
                s -> String.valueOf(new StringBuilder(s).reverse()));

        Learner learner = getLearnerWithNewPassword(
                learnerService.readByEmail(EMAIL).get());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            learnerService.update(HANDLE + "INVALID", learner);
        });
    }


    private static Learner getLearnerWithNewPassword(final Learner existingLearner) {
        return new Learner(
                existingLearner.userHandle(),
                existingLearner.email(),
                String.valueOf(System.currentTimeMillis()),
                existingLearner.imageUrl(),
                existingLearner.provider(),
                existingLearner.createdAt(),
                existingLearner.modifiedAt());
    }

    public static SignupRequest aSignupRequest() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(EMAIL);
        signupRequest.setImageUrl("/images/" + HANDLE + ".png");
        signupRequest.setPassword("password");
        return signupRequest;
    }
}