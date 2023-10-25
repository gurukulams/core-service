package com.gurukulams.core.service;


import com.gurukulams.core.GurukulamsManager;
import com.gurukulams.core.model.Annotation;
import com.gurukulams.core.store.AnnotationStore;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * The type User Annotation service.
 */
public class AnnotationService {

    /**
     * this helps to execute sql queries.
     */
    private final AnnotationStore annotationsStore;

    /**
     * initializes.
     *
     * @param gurukulamsManager
     */
    public AnnotationService(final GurukulamsManager gurukulamsManager) {
        this.annotationsStore = gurukulamsManager.getAnnotationStore();
    }



    /**
     * Create optional.
     *
     * @param userName   user name
     * @param annotation the  annotation
     * @param onType
     * @param onInstance
     * @param locale     tha language
     * @return the optional
     */
    public final Annotation create(final String onType,
                                    final String onInstance,
                                    final Annotation annotation,
                                    final Locale locale,
                                    final String userName) throws SQLException {
        annotation.setId(UUID.randomUUID());
        annotation.setOnType(onType);
        annotation.setOnInstance(onInstance);
        annotation.setCreatedBy(userName);
        if (locale != null) {
            annotation.setLocale(locale.getLanguage());
        }
        return this.annotationsStore
                .insert()
                .values(annotation).returning();
    }


    /**
     * Read optional.
     *
     * @param id     the id
     * @param locale tha language
     * @return the optional
     */
    public final Optional<Annotation> read(final UUID id,
                        final Locale locale) throws SQLException {

        if (locale == null) {
            return this.annotationsStore.select(id, AnnotationStore
                    .locale().isNull());
        } else {
            return this.annotationsStore.select(id, AnnotationStore
                    .locale().eq(locale.getLanguage()));
        }
    }

    /**
     * List list.
     *
     * @param userName   user name
     * @param onInstance the on instance
     * @param onType
     * @param locale     tha language
     * @return the list
     */
    public final List<Annotation> list(final String userName,
                                        final Locale locale,
                                        final String onType,
                                        final String onInstance)
            throws SQLException {
        if (locale == null) {
            return this.annotationsStore
                    .select(AnnotationStore.onType().eq(onType)
                            .and().locale().isNull()
                            .and().onInstance().eq(onInstance)
                            .and().createdBy().eq(userName)).execute();
        } else {
            return this.annotationsStore
                    .select(AnnotationStore.onType().eq(onType)
                            .and().locale().eq(locale.getLanguage())
                            .and().onInstance().eq(onInstance)
                            .and().createdBy().eq(userName)).execute();
        }
    }

    /**
     * Update Annotation optional.
     *
     * @param id         the id
     * @param annotation the user Annotation
     * @param locale     tha language
     * @return the optional
     */
    public final Optional<Annotation> update(final UUID id,
                          final Locale locale,
                          final Annotation annotation) throws SQLException {

        if (id.equals(annotation.getId())) {
            if (locale == null) {
                this.annotationsStore.update()
                    .set(AnnotationStore.note(annotation.getNote()))
                    .where(AnnotationStore.locale().isNull())
                    .execute();
            } else {
                this.annotationsStore.update()
                        .set(AnnotationStore.note(annotation.getNote()))
                    .where(AnnotationStore.locale().eq(locale.getLanguage()))
                    .execute();
            }
            return read(id, locale);
        } else {
            throw new IllegalArgumentException("Ids do not match");
        }
    }

    /**
     * Delete boolean.
     *
     * @param id     the id
     * @param locale tha language
     * @return the boolean
     */
    public final boolean delete(final UUID id, final Locale locale)
            throws SQLException {
        if (locale == null) {
            return this.annotationsStore.delete(
                    AnnotationStore.id().eq(id)
                            .and().locale().isNull()).execute() == 1;
        }
        return this.annotationsStore.delete(
                AnnotationStore.id().eq(id)
                        .and().locale()
                        .eq(locale.getLanguage())).execute() == 1;
    }

    /**
     * Deletes all Annotations.
     */
    public void delete() throws SQLException {
        this.annotationsStore.delete().execute();
    }

}
