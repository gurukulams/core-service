
package com.gurukulams.core.service;

import com.gurukulams.core.model.Tag;
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


class TagServiceTest {


    private final TagService tagService;

    TagServiceTest() {
        this.tagService = new TagService(TestUtil.dataManager());
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
        tagService.delete();
    }


    @Test
    void create() throws SQLException {
        final Tag tag = tagService.create("hari"
                , null, anTag());
        Assertions.assertTrue(tagService.read("hari", tag.id(), null).isPresent(), "Created Tag");
    }

    @Test
    void createLocalized() throws SQLException {
        final Tag tag = tagService.create("hari"
                , Locale.GERMAN, anTag());
        Assertions.assertTrue(tagService.read("hari", tag.id(), Locale.GERMAN).isPresent(), "Created Localized Tag");
        Assertions.assertTrue(tagService.read("hari", tag.id(), null).isPresent(), "Created Tag");
    }

    @Test
    void read() throws SQLException {
        final Tag tag = tagService.create("hari",
                null, anTag());
        Assertions.assertTrue(tagService.read("hari", tag.id(), null).isPresent(),
                "Created Tag");
    }

    @Test
    void update() throws SQLException {

        final Tag tag = tagService.create("hari",
                null, anTag());
//        Tag newTag = new Tag();
//        newTag.setId(UUID.randomUUID().toString());
//        newTag.setTitle("HansiTag");

        Tag newTag = tag.withId(UUID.randomUUID().toString()).withTitle("HansiTag");

        newTag.withId(UUID.randomUUID().toString());
        newTag.withTitle("HansiTag");
        Tag updatedTag = tagService
                .update(tag.id(), "priya", null, newTag);
        Assertions.assertEquals("HansiTag", updatedTag.title(), "Updated");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            tagService
                    .update(UUID.randomUUID().toString(), "priya", null, newTag);
        });
    }

    @Test
    void updateLocalized() throws SQLException {

        final Tag tag = tagService.create("hari",
                null, anTag());
        Tag newTag = tag.withId(tag.id()).withTitle("HansiTag");
        newTag.withId(tag.id());
        newTag.withTitle("HansiTag");
        Tag updatedTag = tagService
                .update(tag.id(), "priya", Locale.GERMAN, newTag);

        Assertions.assertEquals("HansiTag", tagService.read("mani", tag.id(), Locale.GERMAN).get().title(), "Updated");
        Assertions.assertNotEquals("HansiTag", tagService.read("mani", tag.id(), null).get().title(), "Updated");


        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            tagService
                    .update(UUID.randomUUID().toString(), "priya", null, newTag);
        });
    }

    @Test
    void delete() throws SQLException {

        final Tag tag = tagService.create("hari", null,
                anTag());
        tagService.delete("mani", tag.id());
        Assertions.assertFalse(tagService.read("mani", tag.id(), null).isPresent(), "Deleted Tag");
    }

    @Test
    void list() throws SQLException {

        final Tag tag = tagService.create("hari", null,
                anTag());
        Tag newTag = tag.withId(UUID.randomUUID().toString()).withTitle("HansiTag");
        newTag.withId(UUID.randomUUID().toString());
        newTag.withTitle("HansiTag");
        tagService.create("hari", null,
                newTag);
        List<Tag> listofCategories = tagService.list("hari", null);
        Assertions.assertEquals(2, listofCategories.size());

    }

    @Test
    void listLocalized() throws SQLException {

        final Tag tag = tagService.create("hari", Locale.GERMAN,
                anTag());
        Tag newTag = tag.withId(UUID.randomUUID().toString()).withTitle("HansiTag");
        newTag.withId(UUID.randomUUID().toString());
        newTag.withTitle("HansiTag");
        tagService.create("hari", null,
                newTag);
        List<Tag> listofCategories = tagService.list("hari", null);
        Assertions.assertEquals(2, listofCategories.size());

        listofCategories = tagService.list("hari", Locale.GERMAN);
        Assertions.assertEquals(2, listofCategories.size());

    }


    /**
     * Gets practice.
     *
     * @return the practice
     */
    Tag anTag() {
        Tag tag = new Tag(UUID.randomUUID().toString(),"HariTag",
                null,null,"sobhan",
                null,null);

        return tag;
    }
}
