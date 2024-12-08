package com.gurukulams.core.service;

import com.gurukulams.core.DataManager;
import com.gurukulams.core.model.Tag;
import com.gurukulams.core.model.TagLocalized;
import com.gurukulams.core.store.TagLocalizedStore;
import com.gurukulams.core.store.TagStore;

import javax.sql.DataSource;

import static com.gurukulams.core.store.TagStore.id;
import static com.gurukulams.core.store.TagStore.title;
import static com.gurukulams.core.store.TagStore.modifiedBy;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.gurukulams.core.store.TagLocalizedStore.tagId;
import static com.gurukulams.core.store.TagLocalizedStore.locale;

/**
 * The type Tag service.
 */
public class TagService {

    /**
     * Locale Specific Read Query.
     */
    private static final String READ_QUERY = """
            select distinct c.id,
                case when cl.locale = ?
                    then cl.title
                    else c.title
                end as title,
                case when cl.locale = ?
                    then cl.description
                    else c.description
                end as description,
                created_at, created_by, modified_at, modified_by
            from tag c
            left join tag_localized cl on c.id = cl.tag_id
            where cl.locale is null
                or cl.locale = ?
            """;
    /**
     * Datasource for persistence.
     */
    private final DataSource dataSource;
    /**
     * tagStore.
     */
    private final TagStore tagStore;

    /**
     * tagStore.
     */
    private final TagLocalizedStore tagLocalizedStore;

    /**
     * Builds a new Tag service.
     * @param theDataSource
     * @param dataManager database manager.
     */
    public TagService(final DataSource theDataSource,
                      final DataManager dataManager) {
        this.dataSource = theDataSource;
        this.tagStore = dataManager.getTagStore();
        this.tagLocalizedStore
                = dataManager.getTagLocalizedStore();
    }

    /**
     * Create tag.
     *
     * @param userName the username
     * @param locale   the locale
     * @param tag the tag
     * @return the tag
     */
    public Tag create(final String userName,
                                    final Locale locale,
                                    final Tag tag)
            throws SQLException {
        tag.withCreatedBy(userName);
        tag.withCreatedAt(LocalDateTime.now());
        this.tagStore.insert().values(tag).execute(this.dataSource);
        if (locale != null) {
            createLocalized(locale, tag);
        }
        return read(userName, tag.id(), locale).get();
    }

    /**
     * Creates Localized Tag.
     * @param tag
     * @param locale
     * @return localized
     * @throws SQLException
     */
    private int createLocalized(final Locale locale,
                                final Tag tag)
                                    throws SQLException {
        TagLocalized localized = new TagLocalized(tag.id(),
                locale.getLanguage(),
                tag.title(),
                null);
        return this.tagLocalizedStore.insert()
                .values(localized)
                .execute(this.dataSource);
    }

    /**
     * Read optional.
     *
     * @param userName the username
     * @param id       the id
     * @param locale   the locale
     * @return the optional
     */
    public Optional<Tag> read(final String userName,
                                   final String id,
                                   final Locale locale)
            throws SQLException {

        if (locale == null) {
            return this.tagStore.select(this.dataSource,
                    id);
        }

        return tagStore.select()
                .sql(READ_QUERY + " and c.id = ?")
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .param(id(id))
                .optional(this.dataSource);
    }

    /**
     * Update tag.
     *
     * @param id       the id
     * @param userName the username
     * @param locale   the locale
     * @param tag the tag
     * @return the tag
     */
    public Tag update(final String id,
                           final String userName,
                           final Locale locale,
                           final Tag tag) throws SQLException {
        int updatedRows;

        if (locale == null) {
            updatedRows = this.tagStore.update()
                    .set(title(tag.title()),
                            modifiedBy(userName))
                    .where(id().eq(id)).execute(this.dataSource);
        } else {
            updatedRows = this.tagStore.update()
                    .set(modifiedBy(userName))
                    .where(id().eq(id)).execute(this.dataSource);
            if (updatedRows != 0) {
                updatedRows = this.tagLocalizedStore.update().set(
                        title(tag.title()),
                        locale(locale.getLanguage()))
                        .where(tagId().eq(id)
                        .and().locale().eq(locale.getLanguage()))
                        .execute(this.dataSource);

                if (updatedRows == 0) {
                    updatedRows = createLocalized(locale, tag);
                }
            }
        }

        if (updatedRows == 0) {
            throw new IllegalArgumentException("Tag not found");
        }

        return read(userName, id, locale).get();
    }

    /**
     * List list.
     *
     * @param userName the username
     * @param locale   the locale
     * @return the list
     */
    public List<Tag> list(final String userName,
                               final Locale locale) throws SQLException {
        if (locale == null) {
            return this.tagStore.select().execute(this.dataSource);
        }
        return tagStore.select().sql(READ_QUERY)
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .list(this.dataSource);
    }

    /**
     * Delete boolean.
     *
     * @param userName the username
     * @param id       the id
     * @return the boolean
     */
    public boolean delete(final String userName, final String id)
            throws SQLException {
        this.tagLocalizedStore
                .delete()
                .where(tagId().eq(id))
                .execute(this.dataSource);
        return this.tagStore
                    .delete(this.dataSource,
                            id) == 1;
    }

    /**
     * Cleaning up all tag.
     */
    public void delete() throws SQLException {
        this.tagLocalizedStore
                .delete()
                .execute(this.dataSource);
        this.tagStore
                .delete()
                .execute(this.dataSource);
    }
}
