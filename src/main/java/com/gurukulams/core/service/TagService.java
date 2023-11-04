package com.gurukulams.core.service;

import com.gurukulams.core.GurukulamsManager;
import com.gurukulams.core.model.Tag;
import com.gurukulams.core.model.TagLocalized;
import com.gurukulams.core.store.TagLocalizedStore;
import com.gurukulams.core.store.TagStore;
import static com.gurukulams.core.store.TagStore.id;
import static com.gurukulams.core.store.TagStore.title;
import static com.gurukulams.core.store.TagStore.modifiedBy;

import java.sql.SQLException;
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
     * tagStore.
     */
    private final TagStore tagStore;

    /**
     * tagStore.
     */
    private final TagLocalizedStore tagLocalizedStore;

    /**
     * Builds a new Tag service.
     *
     * @param gurukulamsManager database manager.
     */
    public TagService(final GurukulamsManager gurukulamsManager) {
        this.tagStore = gurukulamsManager.getTagStore();
        this.tagLocalizedStore
                = gurukulamsManager.getTagLocalizedStore();
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
        tag.setCreatedBy(userName);
        this.tagStore.insert().values(tag).execute();
        if (locale != null) {
            createLocalized(locale, tag);
        }
        return read(userName, tag.getId(), locale).get();
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
        TagLocalized localized = new TagLocalized();
        localized.setTagId(tag.getId());
        localized.setLocale(locale.getLanguage());
        localized.setTitle(tag.getTitle());
        return this.tagLocalizedStore.insert()
                .values(localized)
                .execute();
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
            return this.tagStore.select(id);
        }

        return tagStore.select()
                .sql(READ_QUERY + " and c.id = ?")
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .param(id(id))
                .optional();
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
                    .set(title(tag.getTitle()),
                            modifiedBy(userName))
                    .where(id().eq(id)).execute();
        } else {
            updatedRows = this.tagStore.update()
                    .set(modifiedBy(userName))
                    .where(id().eq(id)).execute();
            if (updatedRows != 0) {
                updatedRows = this.tagLocalizedStore.update().set(
                        title(tag.getTitle()),
                        locale(locale.getLanguage()))
                        .where(tagId().eq(id)
                        .and().locale().eq(locale.getLanguage())).execute();

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
            return this.tagStore.select().execute();
        }
        return tagStore.select().sql(READ_QUERY)
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .list();
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
                .delete(tagId().eq(id))
                .execute();
        return this.tagStore
                    .delete(id) == 1;
    }

    /**
     * Cleaning up all tag.
     */
    public void delete() throws SQLException {
        this.tagLocalizedStore
                .delete()
                .execute();
        this.tagStore
                .delete()
                .execute();
    }
}
