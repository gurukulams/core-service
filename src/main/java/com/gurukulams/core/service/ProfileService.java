package com.gurukulams.core.service;

import com.gurukulams.core.GurukulamsManager;
import com.gurukulams.core.model.Handle;
import com.gurukulams.core.model.LearnerBuddy;
import com.gurukulams.core.model.LearnerProfile;
import com.gurukulams.core.model.Org;
import com.gurukulams.core.payload.Profile;
import com.gurukulams.core.store.HandleStore;
import com.gurukulams.core.store.LearnerBuddyStore;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.gurukulams.core.store.OrgLocalizedStore.locale;

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
     * LearnerBuddyStore.
     */
    private final LearnerBuddyStore learnerBuddyStore;

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
        this.learnerBuddyStore =
                gurukulamsManager.getLearnerBuddyStore();
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
            return Optional.ofNullable(getProfile(optionalHandle.get()));
        }

        return Optional.empty();
    }

    /**
     * List list.
     *
     * @param userName the username
     * @param locale   the locale
     * @return the list
     */
    public List<Profile> list(final String userName,
                          final Locale locale) throws SQLException {

        List<Handle> handles = this.handleStore.select().execute();

        if (!handles.isEmpty()) {
            List<Profile> profiles = new ArrayList<>(handles.size());
            for (Handle handle:handles) {
                profiles.add(getProfile(handle));
            }
            return profiles;
        }

        return Collections.emptyList();
    }
    /**
     * is the Registered for the given event.
     *
     * @param userName the username
     * @param userToFollow  the userToFollow
     * @return the boolean
     */
    public boolean isRegistered(final String userName,
                                final String userToFollow)
            throws SQLException {
        return this.learnerBuddyStore.exists(userToFollow, userName);
    }
    /**
     * Register for an Org..
     *
     * @param userName the username
     * @param userToFollow       the userToFollow
     * @return the boolean
     */
    public boolean register(final String userName, final String userToFollow)
            throws SQLException {
        Optional<Profile> optionalOrg = this.read(userToFollow);
        if (optionalOrg.isPresent()
                && !userName.equals(userToFollow)) {
            LearnerBuddy learnerBuddy = new LearnerBuddy();
            learnerBuddy.setBuddyHandle(userToFollow);
            learnerBuddy.setLearnerHandle(userName);
            return this.learnerBuddyStore
                    .insert()
                    .values(learnerBuddy)
                    .execute() == 1;
        } else {
            throw new IllegalArgumentException("Learner not found");
        }
    }
    /**
     * get Buddies of an User.
     * @param userName
     * @return orgs
     * @throws SQLException
     */
    public List<Profile> getBuddies(final String userName) throws SQLException {
        List<Profile> orgs = new ArrayList<>();

        for (LearnerBuddy orgLearner : this.learnerBuddyStore
                .select(LearnerBuddyStore.learnerHandle().eq(userName))
                .execute()) {
            orgs.add(this.read(orgLearner.getBuddyHandle()).get());
        }
        return orgs;
    }

    private Profile getProfile(final Handle handle) throws SQLException {
        if (handle.getType().equals("Learner")) {
            Optional<LearnerProfile> learnerProfileOptional =
                    this.learnerProfileService.read(handle.getUserHandle());
            if (learnerProfileOptional.isPresent()) {
                return new Profile(handle.getUserHandle(),
                        learnerProfileOptional.get().getName(),
                        this.learnerService.read(handle.getUserHandle()).get()
                                .imageUrl());
            }
        } else {
            Optional<Org> optionalOrg = this.orgService
                    .read(handle.getUserHandle(), handle.getUserHandle(), null);
            if (optionalOrg.isPresent()) {
                return new Profile(handle.getUserHandle(),
                        optionalOrg.get().getTitle(),
                        optionalOrg.get().getImageUrl());
            }
        }
        return null;
    }
}
