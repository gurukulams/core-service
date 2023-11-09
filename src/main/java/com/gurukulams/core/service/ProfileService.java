package com.gurukulams.core.service;

import com.gurukulams.core.model.LearnerProfile;
import com.gurukulams.core.payload.Profile;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Service Profile relates Requests.
 */
public class ProfileService {

    /**
     * learnerStore.
     */
    private final LearnerService learnerService;
    /**
     * learnerProfileStore.
     */
    private final LearnerProfileService learnerProfileService;

    /**
     * this is the constructor.
     * @param theLearnerProfileService
     * @param theLearnerService
     */
    public ProfileService(final LearnerService theLearnerService,
                      final LearnerProfileService theLearnerProfileService) {
        this.learnerService = theLearnerService;
        this.learnerProfileService = theLearnerProfileService;
    }

    /**
     * Read optional.
     *
     * @param userHandle the user handle
     * @return learner optional
     * @throws SQLException the sql exception
     */
    public Optional<Profile> read(final String userHandle) throws SQLException {
        Optional<LearnerProfile> learnerProfileOptional =
                this.learnerProfileService.read(userHandle);
        if (learnerProfileOptional.isPresent()) {
            return Optional.of(new Profile(
                    learnerProfileOptional.get().getName(),
                    this.learnerService.read(userHandle).get().imageUrl()));
        }
        return Optional.empty();
    }
}
