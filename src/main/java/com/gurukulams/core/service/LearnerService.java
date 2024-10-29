package com.gurukulams.core.service;

import com.gurukulams.core.DataManager;
import com.gurukulams.core.model.Handle;
import com.gurukulams.core.payload.AuthProvider;
import com.gurukulams.core.payload.Learner;
import com.gurukulams.core.payload.SignupRequest;
import com.gurukulams.core.store.HandleStore;
import com.gurukulams.core.store.LearnerBuddyStore;
import com.gurukulams.core.store.LearnerProfileStore;
import com.gurukulams.core.store.LearnerStore;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * The type Learner service.
 */
public class LearnerService {
    /**
     * Datasource for persistence.
     */
    private final DataSource dataSource;
    /**
     * learnerStore.
     */
    private final LearnerStore learnerStore;
    /**
     * learnerProfileStore.
     */
    private final LearnerProfileStore learnerProfileStore;

    /**
     * handleStore.
     */
    private final HandleStore handleStore;
    /**
     * LearnerBuddyStore.
     */
    private final LearnerBuddyStore learnerBuddyStore;
    /**
     * Bean Validator.
     */
    private final jakarta.validation.Validator validator;

    /**
     * this is the constructor.
     * @param theDataSource
     * @param dataManager the gurukulams manager
     * @param pValidator        the p validator
     */
    public LearnerService(final DataSource theDataSource,
                          final DataManager dataManager,
                          final Validator
                                  pValidator) {
        this.dataSource = theDataSource;
        this.validator = pValidator;
        this.learnerStore = dataManager.getLearnerStore();
        this.handleStore = dataManager.getHandleStore();
        this.learnerProfileStore = dataManager.getLearnerProfileStore();
        this.learnerBuddyStore = dataManager.getLearnerBuddyStore();
    }


    /**
     * Sigup an User.
     *
     * @param signUpRequest   the sign up request
     * @param encoderFunction the encoder function
     * @throws SQLException the sql exception
     */
    public void signUp(final SignupRequest signUpRequest,
                       final Function<String, String> encoderFunction)
            throws SQLException {
        Set<ConstraintViolation<SignupRequest>> violations =
                validator.validate(signUpRequest);
        if (violations.isEmpty()) {
            String userHandle = signUpRequest.getEmail().split("@")[0];
            Optional<Handle> handle = createHandle(userHandle);
            if (handle.isPresent()) {
                create(
                        new Learner(userHandle, signUpRequest.getEmail(),
                                encoderFunction
                                        .apply(signUpRequest.getPassword()),
                                signUpRequest.getImageUrl(),
                                signUpRequest
                                        .getAuthProvider(),
                                LocalDateTime.now(),
                                null));
            }
        } else {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<SignupRequest>
                    constraintViolation : violations) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: "
                    + sb, violations);
        }

    }

    /**
     * @param learner
     * @return learner
     */
    private Learner create(final Learner learner) throws SQLException {
        return getLearner(this.learnerStore
                .insert()
                .values(getLearner(learner))
                .returning(this.dataSource));
    }

    /**
     * Read optional.
     *
     * @param userHandle the user handle
     * @return learner optional
     * @throws SQLException the sql exception
     */
    public Optional<Learner> read(final String userHandle) throws SQLException {
        return getLearner(this.learnerStore
                .select(this.dataSource, userHandle));
    }

    /**
     * Read by email optional.
     *
     * @param email the email
     * @return learner optional
     */
    public Optional<Learner> readByEmail(final String email)
            throws SQLException {
        return getLearner(learnerStore.selectByEmail(this.dataSource,
                email));
    }

    /**
     * Update learner.
     *
     * @param userHandle the user handle
     * @param learner    the learner
     * @return learner learner
     * @throws SQLException the sql exception
     */
    public Learner update(final String userHandle,
                          final Learner learner) throws SQLException {

        com.gurukulams.core.model.Learner learnerModel =
                new com.gurukulams.core.model.Learner(userHandle,
                        learner.email(), learner.imageUrl(),
                        learner.provider().toString(),
                        learner.password(),
                        null,
                        null);
        final int updatedRows = learnerStore.update()
                .set(learnerModel)
                .where(LearnerStore.userHandle().eq(userHandle))
                .execute(this.dataSource);
        if (updatedRows == 0) {
            throw new IllegalArgumentException("Learner not found");
        }
        return read(userHandle).get();
    }


    private Optional<Handle> createHandle(final String userHandle)
            throws SQLException {
        Handle handle = new Handle(userHandle, "Learner");
        return Optional.of(this.handleStore.insert()
                .values(handle).returning(this.dataSource));
    }


    /**
     * Deletes Learners.
     *
     * @throws SQLException the sql exception
     */
    public void delete() throws SQLException {
        learnerBuddyStore.delete().execute(this.dataSource);
        learnerProfileStore.delete().execute(this.dataSource);
        learnerStore.delete().execute(this.dataSource);
        handleStore.delete(HandleStore.type().eq("Learner"))
                .execute(this.dataSource);

    }

    private Optional<Learner> getLearner(
            final Optional<com.gurukulams.core.model.Learner> learner) {
        return learner.isEmpty() ? Optional.empty()
                : Optional.of(getLearner(learner.get()));
    }

    private Learner getLearner(
            final com.gurukulams.core.model.Learner learner) {
        return new Learner(
                learner.userHandle(),
                learner.email(),
                learner.pword(),
                learner.imageUrl(),
                AuthProvider.valueOf(learner.provider()),
                learner.createdAt(),
                learner.modifiedAt()
        );
    }

    private com.gurukulams.core.model.Learner getLearner(
            final Learner learner) {
        com.gurukulams.core.model.Learner l =
                new com.gurukulams.core.model.Learner(learner.userHandle(),
                        learner.email(),
                        learner.imageUrl(),
                        learner.provider().toString(),
                        learner.password(),
                        learner.createdAt(),
                        learner.modifiedAt());
        return l;
    }
}
