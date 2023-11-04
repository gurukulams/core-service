package com.gurukulams.core.service;

import com.gurukulams.core.GurukulamsManager;
import com.gurukulams.core.model.Category;
import com.gurukulams.core.model.CategoryLocalized;
import com.gurukulams.core.store.CategoryLocalizedStore;
import com.gurukulams.core.store.CategoryStore;
import static com.gurukulams.core.store.CategoryStore.id;
import static com.gurukulams.core.store.CategoryStore.title;
import static com.gurukulams.core.store.CategoryStore.modifiedBy;

import static com.gurukulams.core.store.CategoryLocalizedStore.locale;
import static com.gurukulams.core.store.CategoryLocalizedStore.categoryId;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * The type Category service.
 */
public class CategoryService {

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
            from category c
            left join category_localized cl on c.id = cl.category_id
            where cl.locale is null
                or cl.locale = ?
            """;
    /**
     * categoryStore.
     */
    private final CategoryStore categoryStore;

    /**
     * categoryStore.
     */
    private final CategoryLocalizedStore categoryLocalizedStore;

    /**
     * Builds a new Category service.
     *
     * @param gurukulamsManager database manager.
     */
    public CategoryService(final GurukulamsManager gurukulamsManager) {
        this.categoryStore = gurukulamsManager.getCategoryStore();
        this.categoryLocalizedStore
                = gurukulamsManager.getCategoryLocalizedStore();
    }

    /**
     * Create category.
     *
     * @param userName the username
     * @param locale   the locale
     * @param category the category
     * @return the category
     */
    public Category create(final String userName,
                                    final Locale locale,
                                    final Category category)
            throws SQLException {
        category.setCreatedBy(userName);
        this.categoryStore.insert().values(category).execute();
        if (locale != null) {
            createLocalized(locale, category);
        }
        return read(userName, category.getId(), locale).get();
    }

    /**
     * Creates Localized Category.
     * @param category
     * @param locale
     * @return localized
     * @throws SQLException
     */
    private int createLocalized(final Locale locale,
                                final Category category)
                                    throws SQLException {
        CategoryLocalized localized = new CategoryLocalized();
        localized.setCategoryId(category.getId());
        localized.setLocale(locale.getLanguage());
        localized.setTitle(category.getTitle());
        return this.categoryLocalizedStore.insert()
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
    public Optional<Category> read(final String userName,
                                   final String id,
                                   final Locale locale)
            throws SQLException {

        if (locale == null) {
            return this.categoryStore.select(id);
        }

        return categoryStore.select()
                .sql(READ_QUERY + " and c.id = ?")
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .param(id(id))
                .optional();
    }

    /**
     * Update category.
     *
     * @param id       the id
     * @param userName the username
     * @param locale   the locale
     * @param category the category
     * @return the category
     */
    public Category update(final String id,
                           final String userName,
                           final Locale locale,
                           final Category category) throws SQLException {
        int updatedRows;

        if (locale == null) {
            updatedRows = this.categoryStore.update()
                    .set(title(category.getTitle()),
                            modifiedBy(userName))
                    .where(id().eq(id)).execute();
        } else {
            updatedRows = this.categoryStore.update()
                    .set(modifiedBy(userName))
                    .where(id().eq(id)).execute();
            if (updatedRows != 0) {
                updatedRows = this.categoryLocalizedStore.update().set(
                        title(category.getTitle()),
                        locale(locale.getLanguage()))
                        .where(categoryId().eq(id)
                        .and().locale().eq(locale.getLanguage())).execute();

                if (updatedRows == 0) {
                    updatedRows = createLocalized(locale, category);
                }
            }
        }

        if (updatedRows == 0) {
            throw new IllegalArgumentException("Category not found");
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
    public List<Category> list(final String userName,
                               final Locale locale) throws SQLException {
        if (locale == null) {
            return this.categoryStore.select().execute();
        }
        return categoryStore.select().sql(READ_QUERY)
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
        this.categoryLocalizedStore
                .delete(categoryId().eq(id))
                .execute();
        return this.categoryStore
                    .delete(id) == 1;
    }

    /**
     * Cleaning up all category.
     */
    public void delete() throws SQLException {
        this.categoryLocalizedStore
                .delete()
                .execute();
        this.categoryStore
                .delete()
                .execute();
    }
}
