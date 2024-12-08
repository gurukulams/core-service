package com.gurukulams.core.service;

import com.gurukulams.core.DataManager;
import com.gurukulams.core.model.Category;
import com.gurukulams.core.model.CategoryLocalized;
import com.gurukulams.core.store.CategoryLocalizedStore;
import com.gurukulams.core.store.CategoryStore;

import javax.sql.DataSource;

import static com.gurukulams.core.store.CategoryStore.id;
import static com.gurukulams.core.store.CategoryStore.title;
import static com.gurukulams.core.store.CategoryStore.modifiedBy;

import static com.gurukulams.core.store.CategoryLocalizedStore.locale;
import static com.gurukulams.core.store.CategoryLocalizedStore.categoryId;

import java.sql.SQLException;
import java.time.LocalDateTime;
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
     * Datasource for persistence.
     */
    private final DataSource dataSource;
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
     * @param theDataSource
     * @param dataManager database manager.
     */
    public CategoryService(final DataSource theDataSource,
                           final DataManager dataManager) {
        this.dataSource = theDataSource;
        this.categoryStore = dataManager.getCategoryStore();
        this.categoryLocalizedStore
                = dataManager.getCategoryLocalizedStore();
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
        category.withCreatedBy(userName);
        category.withCreatedAt(LocalDateTime.now());
        this.categoryStore.insert().values(category).execute(this.dataSource);
        if (locale != null) {
            createLocalized(locale, category);
        }
        return read(userName, category.id(), locale).get();
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
        CategoryLocalized localized = new CategoryLocalized(category.id(),
                locale.getLanguage(),
                category.title(),
                null);
        return this.categoryLocalizedStore.insert()
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
    public Optional<Category> read(final String userName,
                                   final String id,
                                   final Locale locale)
            throws SQLException {

        if (locale == null) {
            return this.categoryStore.select(this.dataSource,
                    id);
        }

        return categoryStore.select()
                .sql(READ_QUERY + " and c.id = ?")
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .param(locale(locale.getLanguage()))
                .param(id(id))
                .optional(this.dataSource);
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
                    .set(title(category.title()),
                            modifiedBy(userName))
                    .where(id().eq(id)).execute(this.dataSource);
        } else {
            updatedRows = this.categoryStore.update()
                    .set(modifiedBy(userName))
                    .where(id().eq(id)).execute(this.dataSource);
            if (updatedRows != 0) {
                updatedRows = this.categoryLocalizedStore.update().set(
                        title(category.title()),
                        locale(locale.getLanguage()))
                        .where(categoryId().eq(id)
                        .and().locale().eq(locale.getLanguage()))
                        .execute(this.dataSource);

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
            return this.categoryStore.select().execute(this.dataSource);
        }
        return categoryStore.select().sql(READ_QUERY)
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
        this.categoryLocalizedStore
                .delete()
                .where(categoryId().eq(id))
                .execute(this.dataSource);
        return this.categoryStore
                    .delete(this.dataSource, id) == 1;
    }

    /**
     * Cleaning up all category.
     */
    public void delete() throws SQLException {
        this.categoryLocalizedStore
                .delete()
                .execute(this.dataSource);
        this.categoryStore
                .delete()
                .execute(this.dataSource);
    }
}
