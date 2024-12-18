package com.gurukulams.core.service;

import com.gurukulams.core.DataManager;
import com.gurukulams.core.model.Handle;
import com.gurukulams.core.model.Org;
import com.gurukulams.core.model.OrgLearner;
import com.gurukulams.core.model.OrgLocalized;
import com.gurukulams.core.store.HandleStore;
import com.gurukulams.core.store.OrgLearnerStore;
import com.gurukulams.core.store.OrgLocalizedStore;
import com.gurukulams.core.store.OrgStore;

import javax.sql.DataSource;

import static com.gurukulams.core.store.OrgLocalizedStore.locale;
import static com.gurukulams.core.store.OrgStore.userHandle;
import static com.gurukulams.core.store.OrgStore.title;
import static com.gurukulams.core.store.OrgStore.description;
import static com.gurukulams.core.store.OrgStore.imageUrl;
import static com.gurukulams.core.store.OrgStore.modifiedBy;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * The type Org service.
 */
public class OrgService {

    /**
     * Locale Specific Read Query.
     */
    private static final String READ_QUERY = """
            select distinct c.user_handle,
                case when cl.locale = ?
                    then cl.title
                    else c.title
                end as title,
                case when cl.locale = ?
                    then cl.description
                    else c.description
                end as description,org_type, image_url,
                created_at, created_by, modified_at, modified_by
            from org c
            left join org_localized cl on c.user_handle = cl.user_handle
            where cl.locale is null
                or cl.locale = ?
            """;
    /**
     * Type of Handle for Org.
     */
    private static final String HANDLE_TYPE = "org";

    /**
     * Separator for Handle.
     */
    private static final String SEPARATOR = "-";
    /**
     * Datasource for persistence.
     */
    private final DataSource dataSource;
    /**
     * orgStore.
     */
    private final OrgStore orgStore;

    /**
     * orgStore.
     */
    private final OrgLocalizedStore orgLocalizedStore;

    /**
     * handleStore.
     */
    private final HandleStore handleStore;

    /**
     * orgLearnerStore.
     */
    private final OrgLearnerStore orgLearnerStore;

    /**
     * Builds a new Org service.
     * @param theDataSource
     * @param dataManager database manager.
     */
    public OrgService(final DataSource theDataSource,
                      final DataManager dataManager) {
        this.dataSource = theDataSource;
        this.orgStore = dataManager.getOrgStore();
        this.orgLocalizedStore
                = dataManager.getOrgLocalizedStore();
        this.handleStore
                = dataManager.getHandleStore();
        this.orgLearnerStore
                = dataManager.getOrgLearnerStore();
    }

    /**
     * Create org.
     *
     * @param userName the username
     * @param locale   the locale
     * @param organization the org
     * @return the org
     */
    public Org create(final String userName,
                           final Locale locale,
                           final Org organization)
            throws SQLException {
        Org org = organization
                .withUserHandle(HANDLE_TYPE + SEPARATOR
                        + organization.userHandle());

        Handle handle = new Handle(org.userHandle(),
                HANDLE_TYPE);
        this.handleStore
                .insert()
                        .values(handle)
                                .execute(this.dataSource);
        org = org.withCreatedBy(userName);
        org = org.withCreatedAt(LocalDateTime.now());
        this.orgStore.insert().values(org).execute(this.dataSource);
        if (locale != null) {
            createLocalized(locale, org);
        }
        return read(userName, org.userHandle(), locale).get();
    }

    /**
     * Creates Localized Org.
     * @param org
     * @param locale
     * @return localized
     * @throws SQLException
     */
    private int createLocalized(final Locale locale,
                                final Org org)
            throws SQLException {
        OrgLocalized localized = new OrgLocalized(org.userHandle(),
                locale.getLanguage(),
                org.title(),
                org.description());
        return this.orgLocalizedStore.insert()
                .values(localized)
                .execute(this.dataSource);
    }

    /**
     * Read optional.
     *
     * @param userName the username
     * @param userHandle       the userHandle
     * @param locale   the locale
     * @return the optional
     */
    public Optional<Org> read(final String userName,
                                   final String userHandle,
                                   final Locale locale)
            throws SQLException {

        if (locale == null) {
            return this.orgStore.select(this.dataSource,
                    userHandle);
        }

        return orgStore.select()
                .sql(READ_QUERY + " and c.user_handle = ?")
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .param(userHandle(userHandle))
                .optional(this.dataSource);
    }

    /**
     * Update org.
     *
     * @param userHandle       the userHandle
     * @param userName the username
     * @param locale   the locale
     * @param org the org
     * @return the org
     */
    public Org update(final String userHandle,
                           final String userName,
                           final Locale locale,
                           final Org org) throws SQLException {
        int updatedRows;

        if (locale == null) {
            updatedRows = this.orgStore.update()
                    .set(title(org.title()),
                            description(org.description()),
                            imageUrl(org.imageUrl()),
                            modifiedBy(userName))
                    .where(userHandle().eq(userHandle))
                    .execute(this.dataSource);
        } else {
            updatedRows = this.orgStore.update()
                    .set(modifiedBy(userName))
                    .where(userHandle().eq(userHandle))
                    .execute(this.dataSource);
            if (updatedRows != 0) {
                updatedRows = this.orgLocalizedStore.update().set(
                                title(org.title()),
                                description(org.description()),
                                locale(locale.getLanguage()))
                        .where(OrgLocalizedStore.userHandle().eq(userHandle)
                                .and().locale().eq(locale.getLanguage()))
                        .execute(this.dataSource);

                if (updatedRows == 0) {
                    updatedRows = createLocalized(locale, org);
                }
            }
        }

        if (updatedRows == 0) {
            throw new IllegalArgumentException("Org not found");
        }

        return read(userName, userHandle, locale).get();
    }

    /**
     * List list.
     *
     * @param userName the username
     * @param locale   the locale
     * @return the list
     */
    public List<Org> list(final String userName,
                               final Locale locale) throws SQLException {
        if (locale == null) {
            return this.orgStore.select().execute(this.dataSource);
        }
        return orgStore.select().sql(READ_QUERY)
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .list(this.dataSource);
    }

    /**
     * get Organizations of an User.
     * @param userName
     * @param locale
     * @return orgs
     * @throws SQLException
     */
    public List<Org> getOrganizationsOf(final String userName,
                                      final Locale locale) throws SQLException {
        List<Org> orgs = new ArrayList<>();

        for (OrgLearner orgLearner : this.orgLearnerStore
                .select().where(OrgLearnerStore.learnerHandle().eq(userName))
                .execute(this.dataSource)) {
            orgs.add(this.read(userName, orgLearner.orgHandle(),
                    locale).get());
        }

        return orgs;
    }

    /**
     * is the Registered for the given event.
     *
     * @param userName the username
     * @param orgHanle  the orgHanle
     * @return the boolean
     */
    public boolean isRegistered(final String userName,
                                final String orgHanle)
            throws SQLException {
        return this.orgLearnerStore.exists(this.dataSource,
                orgHanle, userName);
    }

    /**
     * Register for an Org..
     *
     * @param userName the username
     * @param orgHanle       the orgHanle
     * @return the boolean
     */
    public boolean register(final String userName, final String orgHanle)
            throws SQLException {
        Optional<Org> optionalOrg = this.read(userName, orgHanle, null);
        if (optionalOrg.isPresent()
                && !optionalOrg.get().createdBy().equals(userName)) {
            OrgLearner eventLearner = new OrgLearner(orgHanle,
                    userName);
            return this.orgLearnerStore
                    .insert()
                    .values(eventLearner)
                    .execute(this.dataSource) == 1;
        } else {
            throw new IllegalArgumentException("Org not found");
        }
    }

    /**
     * Delete boolean.
     *
     * @param userName the username
     * @param orgHandle       the orgHandle
     * @return the boolean
     */
    public boolean delete(final String userName, final String orgHandle)
            throws SQLException {

        this.orgLocalizedStore
                .delete()
                .where(OrgLocalizedStore.userHandle().eq(orgHandle))
                .execute(this.dataSource);
        this.orgLearnerStore
                .delete()
                .where(OrgLearnerStore.orgHandle().eq(orgHandle))
                .execute(this.dataSource);
        this.orgStore
                .delete(this.dataSource,
                        orgHandle);
        return this.handleStore
                .delete()
                .where(HandleStore.userHandle().eq(orgHandle))
                .execute(this.dataSource) == 1;
    }

    /**
     * Cleaning up all org.
     */
    public void delete() throws SQLException {
        this.orgLocalizedStore
                .delete()
                .execute(this.dataSource);
        this.orgLearnerStore
                .delete()
                .execute(this.dataSource);
        this.orgStore
                .delete()
                .execute(this.dataSource);
        this.handleStore
                .delete()
                .where(HandleStore.type().eq(HANDLE_TYPE))
                .execute(this.dataSource);
    }
}
