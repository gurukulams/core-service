package com.gurukulams.core.service;

import com.gurukulams.core.GurukulamsManager;
import com.gurukulams.core.model.Handle;
import com.gurukulams.core.model.LearnerProfile;
import com.gurukulams.core.model.Org;
import com.gurukulams.core.payload.Profile;
import com.gurukulams.core.store.HandleStore;

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
     * orgService.
     */
    private final OrgService orgService;

    /**
     * handleStore.
     */
    private final HandleStore handleStore;

    /**
     * this is the constructor.
     *
     * @param gurukulamsManager
     * @param theLearnerService
     * @param theLearnerProfileService
     * @param theorgService
     */
    public ProfileService(final GurukulamsManager gurukulamsManager,
                          final LearnerService theLearnerService,
                          final LearnerProfileService theLearnerProfileService,
                          final OrgService theorgService) {
        this.learnerService = theLearnerService;
        this.learnerProfileService = theLearnerProfileService;
        this.handleStore =
                gurukulamsManager.getHandleStore();
        this.orgService = theorgService;
    }

    /**
     * Read optional.
     *
     * @param userHandle the user handle
     * @return learner optional
     * @throws SQLException the sql exception
     */
    public Optional<Profile> read(final String userHandle) throws SQLException {

        Optional<Handle> optionalHandle = this.handleStore.select(userHandle);

        if (optionalHandle.isPresent()) {
            if (optionalHandle.get().getType().equals("Learner")) {
                Optional<LearnerProfile> learnerProfileOptional =
                        this.learnerProfileService.read(userHandle);
                if (learnerProfileOptional.isPresent()) {
                    return Optional.of(new Profile(
                            learnerProfileOptional.get().getName(),
                            this.learnerService.read(userHandle).get()
                                    .imageUrl()));
                }
            } else {
                Optional<Org> optionalOrg = this.orgService
                        .read(userHandle, userHandle, null);
                if (optionalOrg.isPresent()) {
                    return Optional.of(new Profile(
                            optionalOrg.get().getTitle(),
                            optionalOrg.get().getImageUrl()));
                }
            }

        }


        return Optional.empty();
    }
}
