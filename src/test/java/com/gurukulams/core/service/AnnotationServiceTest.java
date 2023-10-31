package com.gurukulams.core.service;

import com.gurukulams.core.model.Annotation;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;


class AnnotationServiceTest {

    public static final String USER_NAME = "mani";
    private final AnnotationService annotationService;

    AnnotationServiceTest() {
        this.annotationService = new AnnotationService();
    }

    /**
     * Before.
     *
     * @throws SQLException the io exception
     */
    @BeforeEach
    void before() throws SQLException, IOException {
        cleanUp();
    }

    /**
     * After.
     */
    @AfterEach
    void after() throws SQLException, IOException {
        cleanUp();
    }

    private void cleanUp() throws SQLException, IOException {
        annotationService.delete(USER_NAME);
    }

    @Test
    void create() throws SQLException, IOException {
        final Annotation annotation = annotationService.create(
                USER_NAME, USER_NAME, anAnnotation(), null, USER_NAME
        );
        Assertions.assertTrue(annotationService.read(USER_NAME,annotation.getId(),
                        null).isPresent(),
                "Created Annotation");
    }

    @Test
    void read() throws SQLException, IOException {
        final Annotation annotation = annotationService.create(
                USER_NAME, USER_NAME, anAnnotation(), null, USER_NAME
        );
        final UUID newAnnotationId = annotation.getId();
        Assertions.assertTrue(annotationService.read(USER_NAME,newAnnotationId, null).isPresent(),
                "Annotation Created");

        Assertions.assertTrue(annotationService.read(USER_NAME,newAnnotationId,
                        Locale.GERMAN).isEmpty(),
                "Annotation Unavailable for Locale");
    }

    @Test
    void readLocalized() throws SQLException, IOException {
        final Annotation annotation = annotationService.create(
                USER_NAME, USER_NAME, anAnnotation(), Locale.GERMAN, USER_NAME
        );
        final UUID newAnnotationId = annotation.getId();
        Assertions.assertTrue(annotationService.read(USER_NAME,newAnnotationId, null).isEmpty(),
                "Annotation Unavailable for English");

        Assertions.assertTrue(annotationService.read(USER_NAME,newAnnotationId,
                        Locale.GERMAN).isPresent(),
                "Annotation Available for Locale");
    }

    @Test
    void list() throws SQLException, IOException {

        testList(null);
        testList(Locale.GERMAN);

    }

    void testList(Locale locale) throws SQLException, IOException {
        annotationService.create(
                USER_NAME, USER_NAME, anAnnotation(), locale, USER_NAME
        );
        Annotation newAnnotation = new Annotation();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("a", "a");
        jsonObject.put("b", "b2");
        newAnnotation.setNote(jsonObject);
        annotationService.create(USER_NAME, USER_NAME, newAnnotation, locale, USER_NAME
        );
        List<Annotation> listofannotation = annotationService.list(USER_NAME,
                locale, USER_NAME,
                USER_NAME
        );
        Assertions.assertEquals(2, listofannotation.size());
    }
    @Test
    void update() throws SQLException, IOException {
        testUpdate(null);
        testUpdate(Locale.GERMAN);
    }

    void testUpdate(Locale locale) throws SQLException, IOException {
        final Annotation annotation = annotationService.create(
                USER_NAME, USER_NAME, anAnnotation(), locale, USER_NAME
        );
        final UUID newAnnotationId = annotation.getId();

        Annotation newAnnotation = new Annotation();

        newAnnotation.setId(newAnnotationId);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("a", "a");
        jsonObject.put("b", "b2");
        newAnnotation.setNote(jsonObject);

        Optional<Annotation> updatedAnnotation = annotationService
                .update(USER_NAME, newAnnotationId, locale, newAnnotation);
        Assertions.assertEquals("b2", updatedAnnotation.get()
                .getNote().get("b"), "Updated");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            annotationService
                    .update(USER_NAME, UUID.randomUUID(), locale, newAnnotation);
        });
    }
    @Test
    void delete() throws SQLException, IOException {
        testDelete(null);
        testDelete(Locale.GERMAN);
    }

    void testDelete(Locale locale) throws SQLException, IOException {
        final Annotation annotation = annotationService.create(
                USER_NAME, USER_NAME, anAnnotation(), locale, USER_NAME
        );
        annotationService.delete(USER_NAME, annotation.getId(), locale);
        Assertions.assertFalse(annotationService.read(USER_NAME,annotation.getId(), locale).isPresent(),
                "Deleted Annotation");
    }

    private Annotation anAnnotation() {
        Annotation annotation = new Annotation();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("a", "a");
        jsonObject.put("b", "b");
        annotation.setNote(jsonObject);
        return annotation;
    }

}