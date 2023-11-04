package com.gurukulams.core.service;

import com.gurukulams.core.GurukulamsManager;
import com.gurukulams.core.model.LearnerProfile;
import com.gurukulams.core.payload.RegistrationRequest;
import com.gurukulams.core.store.LearnerProfileStore;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

/**
 * The type Learner profile service.
 */
public class LearnerProfileService {

    /**
     * jdbcClient.
     */
    private final LearnerProfileStore learnerProfileStore;

    /**
     * Bean Validator.
     */
    private final jakarta.validation.Validator validator;

    /**
     * Instantiates a new Learner profile service.
     *
     * @param gurukulamsManager the jdbc client
     * @param pValidator
     */
    public LearnerProfileService(
            final GurukulamsManager gurukulamsManager,
            final Validator
                    pValidator) {
        this.learnerProfileStore =
                gurukulamsManager.getLearnerProfileStore();
        this.validator = pValidator;
    }

    /**
     * Create learner profile.
     * @param userName
     * @param registrationRequest the learner profile
     * @return the learner profile
     */
    public LearnerProfile create(final String userName,
                                 final RegistrationRequest registrationRequest)
            throws SQLException {
        Set<ConstraintViolation<RegistrationRequest>> violations =
                validator.validate(registrationRequest);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        LearnerProfile learnerProfile = new LearnerProfile();
        learnerProfile.setUserHandle(userName);
        learnerProfile.setName(registrationRequest.getName());
        learnerProfile.setDob(registrationRequest.getDob());

        return this.learnerProfileStore.insert()
                .values(learnerProfile).returning();
    }

    /**
     * Read optional.
     *
     * @param userHandle the user handle
     * @return the optional
     */
    public Optional<LearnerProfile> read(final String userHandle)
            throws SQLException {
            return this.learnerProfileStore.select(userHandle);

    }

    /**
     * Deletes Profiles.
     * @throws SQLException
     */
    public void delete() throws SQLException {
        this.learnerProfileStore.delete().execute();
    }
}
