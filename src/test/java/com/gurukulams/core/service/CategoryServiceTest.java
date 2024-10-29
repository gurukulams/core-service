
package com.gurukulams.core.service;

import com.gurukulams.core.model.Category;
import com.gurukulams.core.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


class CategoryServiceTest {


    private final CategoryService categoryService;

    CategoryServiceTest() {
        this.categoryService = new CategoryService(TestUtil.getDataSource(), TestUtil.dataManager());
    }

    /**
     * Before.
     *
     * @throws IOException the io exception
     */
    @BeforeEach
    void before() throws SQLException {
        cleanUp();
    }

    /**
     * After.
     */
    @AfterEach
    void after() throws SQLException {
        cleanUp();
    }

    private void cleanUp() throws SQLException {
        categoryService.delete();
    }


    @Test
    void create() throws SQLException {
        final Category category = categoryService.create("hari"
                , null, anCategory());
        Assertions.assertTrue(categoryService.read("hari", category.id(), null).isPresent(), "Created Category");
    }

    @Test
    void createLocalized() throws SQLException {
        final Category category = categoryService.create("hari"
                , Locale.GERMAN, anCategory());
        Assertions.assertTrue(categoryService.read("hari", category.id(), Locale.GERMAN).isPresent(), "Created Localized Category");
        Assertions.assertTrue(categoryService.read("hari", category.id(), null).isPresent(), "Created Category");
    }

    @Test
    void read() throws SQLException {
        final Category category = categoryService.create("hari",
                null, anCategory());
        Assertions.assertTrue(categoryService.read("hari", category.id(), null).isPresent(),
                "Created Category");
    }

    @Test
    void update() throws SQLException {

        final Category category = categoryService.create("hari",
                null, anCategory());
        Category newCategory = category.withId(UUID.randomUUID().toString()).withTitle("HansiCategory");
        newCategory.withId(UUID.randomUUID().toString());
        newCategory.withTitle("HansiCategory");
        Category updatedCategory = categoryService
                .update(category.id(), "priya", null, newCategory);
        Assertions.assertEquals("HansiCategory", updatedCategory.title(), "Updated");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            categoryService
                    .update(UUID.randomUUID().toString(), "priya", null, newCategory);
        });
    }

    @Test
    void updateLocalized() throws SQLException {

        final Category category = categoryService.create("hari",
                null, anCategory());
        Category newCategory = category.withTitle("HansiCategory");
        Category updatedCategory = categoryService
                .update(category.id(), "priya", Locale.GERMAN, newCategory);

        Assertions.assertEquals("HansiCategory", categoryService.read("mani", category.id(), Locale.GERMAN).get().title(), "Updated");
        Assertions.assertNotEquals("HansiCategory", categoryService.read("mani", category.id(), null).get().title(), "Updated");


        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            categoryService
                    .update(UUID.randomUUID().toString(), "priya", null, newCategory);
        });
    }

    @Test
    void delete() throws SQLException {

        final Category category = categoryService.create("hari", null,
                anCategory());
        categoryService.delete("mani", category.id());
        Assertions.assertFalse(categoryService.read("mani", category.id(), null).isPresent(), "Deleted Category");
    }

    @Test
    void list() throws SQLException {

        final Category category = categoryService.create("hari", null,
                anCategory());
        Category newCategory = category.withId(UUID.randomUUID().toString()).withTitle("HansiCategory");
        newCategory.withId(UUID.randomUUID().toString());
        newCategory.withTitle("HansiCategory");
        categoryService.create("hari", null,
                newCategory);
        List<Category> listofCategories = categoryService.list("hari", null);
        Assertions.assertEquals(2, listofCategories.size());

    }

    @Test
    void listLocalized() throws SQLException {

        final Category category = categoryService.create("hari", Locale.GERMAN,
                anCategory());
        Category newCategory = category.withId(UUID.randomUUID().toString()).withTitle("HansiCategory");
        newCategory.withId(UUID.randomUUID().toString());
        newCategory.withTitle("HansiCategory");
        categoryService.create("hari", null,
                newCategory);
        List<Category> listofCategories = categoryService.list("hari", null);
        Assertions.assertEquals(2, listofCategories.size());

        listofCategories = categoryService.list("hari", Locale.GERMAN);
        Assertions.assertEquals(2, listofCategories.size());

    }


    /**
     * Gets practice.
     *
     * @return the practice
     */
    Category anCategory() {
        Category category = new Category(UUID.randomUUID().toString(),"HariCategory",
                null,
                null,
                "sobhan",
                null,
                null);

        return category;
    }
}
