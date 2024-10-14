package com.gurukulams.core.service;

import com.gurukulams.core.DataManager;
import com.gurukulams.core.model.LearnerProfile;
import com.gurukulams.core.payload.RegistrationRequest;
import com.gurukulams.core.store.LearnerProfileStore;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;

import java.lang.annotation.ElementType;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The type Learner profile service.
 */
public class LearnerProfileService {

    /**
     * Max Age of Learner.
     */
    private static final int MAX_AGE = 80;
    /**
     * Min Age of Learner.
     */
    private static final int MIN_AGE = 10;
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
     * @param dataManager the jdbc client
     * @param pValidator
     */
    public LearnerProfileService(
            final DataManager dataManager,
            final Validator
                    pValidator) {
        this.learnerProfileStore =
                dataManager.getLearnerProfileStore();
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
                isValidRegistration(registrationRequest);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }


        LearnerProfile learnerProfile = new LearnerProfile(userName,
                registrationRequest.getName(),
                registrationRequest.getDob());
        return this.learnerProfileStore.insert()
                .values(learnerProfile).returning();
    }

    private Set<ConstraintViolation<RegistrationRequest>>
    isValidRegistration(final RegistrationRequest registrationRequest) {
        Set<ConstraintViolation<RegistrationRequest>> violations =
                new HashSet<>(validator.validate(registrationRequest));
        if (violations.isEmpty()) {
            int age = Period.between(registrationRequest.getDob(),
                    LocalDate.now()).getYears();
            // Age should be in between MIN_AGE to MAX_AGE
            if (age > MAX_AGE || age < MIN_AGE) {
                final String messageTemplate = null;
                final Class<RegistrationRequest> rootBeanClass
                        = RegistrationRequest.class;
                final Object leafBeanInstance = null;
                final Object cValue = null;
                final Path propertyPath = null;
                final ConstraintDescriptor<?> constraintDescriptor = null;
                final ElementType elementType = null;
                final Map<String, Object> messageParameters = new HashMap<>();
                final Map<String, Object> expressionVariables = new HashMap<>();
                ConstraintViolation<RegistrationRequest> violation
                        = ConstraintViolationImpl.forBeanValidation(
                        messageTemplate, messageParameters,
                        expressionVariables,
                        "Age should be in between 10 to 70",
                        rootBeanClass,
                        registrationRequest, leafBeanInstance,
                        cValue, propertyPath,
                        constraintDescriptor, elementType);
                violations.add(violation);
            }
        }

        return violations;
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
