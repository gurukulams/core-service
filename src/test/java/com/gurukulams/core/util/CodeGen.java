package com.gurukulams.core.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.lang.StringTemplate.RAW;

public class CodeGen {
    public static void main(String[] args) throws IOException {

        String name = "Event";
        String pluralName = getPluralName(name);

        generateDDL(name, pluralName);
        generateService(name, pluralName);
        generateServiceTest(name, pluralName);
    }

    private static String getPluralName(String name) {
        if (name.endsWith("y")) {
            return name.substring(0,name.length()-1) + "ies";
        }
        return name + "s";
    }

    private static void generateServiceTest(String name,String pluralName) throws IOException {

        StringTemplate SERVICE_TEST_TEMPLATE = RAW
                ."""

                package com.gurukulams.core.service;

                import com.gurukulams.core.model.\{name};
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


                class \{name}ServiceTest {


                    private final \{name}Service \{name.toLowerCase()}Service;

                    \{name}ServiceTest() {
                        this.\{name.toLowerCase()}Service = new \{name}Service(TestUtil.gurukulamsManager());
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
                        \{name.toLowerCase()}Service.delete();
                    }


                    @Test
                    void create() throws SQLException {
                        final \{name} \{name.toLowerCase()} = \{name.toLowerCase()}Service.create("hari"
                                , null, an\{name}());
                        Assertions.assertTrue(\{name.toLowerCase()}Service.read("hari", \{name.toLowerCase()}.getId(), null).isPresent(), "Created \{name}");
                    }

                    @Test
                    void createLocalized() throws SQLException {
                        final \{name} \{name.toLowerCase()} = \{name.toLowerCase()}Service.create("hari"
                                , Locale.GERMAN, an\{name}());
                        Assertions.assertTrue(\{name.toLowerCase()}Service.read("hari", \{name.toLowerCase()}.getId(), Locale.GERMAN).isPresent(), "Created Localized \{name}");
                        Assertions.assertTrue(\{name.toLowerCase()}Service.read("hari", \{name.toLowerCase()}.getId(), null).isPresent(), "Created \{name}");
                    }

                    @Test
                    void read() throws SQLException {
                        final \{name} \{name.toLowerCase()} = \{name.toLowerCase()}Service.create("hari",
                                null, an\{name}());
                        Assertions.assertTrue(\{name.toLowerCase()}Service.read("hari", \{name.toLowerCase()}.getId(), null).isPresent(),
                                "Created \{name}");
                    }

                    @Test
                    void update() throws SQLException {

                        final \{name} \{name.toLowerCase()} = \{name.toLowerCase()}Service.create("hari",
                                null, an\{name}());
                        \{name} new\{name} = new \{name}();
                        new\{name}.setId(UUID.randomUUID());
                        new\{name}.setTitle("Hansi\{name}");
                        \{name} updated\{name} = \{name.toLowerCase()}Service
                                .update(\{name.toLowerCase()}.getId(), "priya", null, new\{name});
                        Assertions.assertEquals("Hansi\{name}", updated\{name}.getTitle(), "Updated");

                        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                            \{name.toLowerCase()}Service
                                    .update(UUID.randomUUID(), "priya", null, new\{name});
                        });
                    }

                    @Test
                    void updateLocalized() throws SQLException {

                        final \{name} \{name.toLowerCase()} = \{name.toLowerCase()}Service.create("hari",
                                null, an\{name}());
                        \{name} new\{name} = new \{name}();
                        new\{name}.setId(\{name.toLowerCase()}.getId());
                        new\{name}.setTitle("Hansi\{name}");
                        \{name} updated\{name} = \{name.toLowerCase()}Service
                                .update(\{name.toLowerCase()}.getId(), "priya", Locale.GERMAN, new\{name});

                        Assertions.assertEquals("Hansi\{name}", \{name.toLowerCase()}Service.read("mani", \{name.toLowerCase()}.getId(), Locale.GERMAN).get().getTitle(), "Updated");
                        Assertions.assertNotEquals("Hansi\{name}", \{name.toLowerCase()}Service.read("mani", \{name.toLowerCase()}.getId(), null).get().getTitle(), "Updated");


                        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                            \{name.toLowerCase()}Service
                                    .update(UUID.randomUUID(), "priya", null, new\{name});
                        });
                    }

                    @Test
                    void delete() throws SQLException {

                        final \{name} \{name.toLowerCase()} = \{name.toLowerCase()}Service.create("hari", null,
                                an\{name}());
                        \{name.toLowerCase()}Service.delete("mani", \{name.toLowerCase()}.getId());
                        Assertions.assertFalse(\{name.toLowerCase()}Service.read("mani", \{name.toLowerCase()}.getId(), null).isPresent(), "Deleted \{name}");
                    }

                    @Test
                    void list() throws SQLException {

                        final \{name} \{name.toLowerCase()} = \{name.toLowerCase()}Service.create("hari", null,
                                an\{name}());
                        \{name} new\{name} = new \{name}();
                        new\{name}.setId(UUID.randomUUID());
                        new\{name}.setTitle("Hansi\{name}");
                        \{name.toLowerCase()}Service.create("hari", null,
                                new\{name});
                        List<\{name}> listof\{pluralName} = \{name.toLowerCase()}Service.list("hari", null);
                        Assertions.assertEquals(2, listof\{pluralName}.size());

                    }

                    @Test
                    void listLocalized() throws SQLException {

                        final \{name} \{name.toLowerCase()} = \{name.toLowerCase()}Service.create("hari", Locale.GERMAN,
                                an\{name}());
                        \{name} new\{name} = new \{name}();
                        new\{name}.setId(UUID.randomUUID());
                        new\{name}.setTitle("Hansi\{name}");
                        \{name.toLowerCase()}Service.create("hari", null,
                                new\{name});
                        List<\{name}> listof\{pluralName} = \{name.toLowerCase()}Service.list("hari", null);
                        Assertions.assertEquals(2, listof\{pluralName}.size());

                        listof\{pluralName} = \{name.toLowerCase()}Service.list("hari", Locale.GERMAN);
                        Assertions.assertEquals(2, listof\{pluralName}.size());

                    }


                    /**
                     * Gets practice.
                     *
                     * @return the practice
                     */
                    \{name} an\{name}() {
                        \{name} \{name.toLowerCase()} = new \{name}();
                        \{name.toLowerCase()}.setId(UUID.randomUUID());
                        \{name.toLowerCase()}.setTitle("Hari\{name}");
                        return \{name.toLowerCase()};
                    }
                }
                """;
        Files.write(
                Paths.get(RAW. "src/test/java/com/gurukulams/core/service/\{name}ServiceTest.java".interpolate()),
                SERVICE_TEST_TEMPLATE.interpolate().getBytes(),
                StandardOpenOption.CREATE);
    }
    private static void generateService(String name,String pluralName) throws IOException {
        StringTemplate SERVICE_TEMPLATE = RAW
                ."""
                package com.gurukulams.core.service;

                import com.gurukulams.core.GurukulamsManager;
                import com.gurukulams.core.model.\{name};
                import com.gurukulams.core.model.\{name}Localized;
                import com.gurukulams.core.store.\{name}LocalizedStore;
                import com.gurukulams.core.store.\{name}Store;


                import java.sql.SQLException;
                import java.util.List;
                import java.util.Locale;
                import java.util.Optional;
                import java.util.UUID;

                /**
                 * The type \{name} service.
                 */
                public class \{name}Service {

                    /**
                     * \{name.toLowerCase()}Store.
                     */
                    private final \{name}Store \{name.toLowerCase()}Store;

                    /**
                     * \{name.toLowerCase()}Store.
                     */
                    private final \{name}LocalizedStore \{name.toLowerCase()}LocalizedStore;


                    /**
                     * Instantiates a new \{name} service.
                     *
                     * @param gurukulamsManager
                     */
                    public \{name}Service(final GurukulamsManager gurukulamsManager) {
                        this.\{name.toLowerCase()}Store = gurukulamsManager.get\{name}Store();
                        this.\{name.toLowerCase()}LocalizedStore
                                = gurukulamsManager.get\{name}LocalizedStore();
                    }


                    /**
                     * Create \{name.toLowerCase()}.
                     *
                     * @param userName the user name
                     * @param locale   the locale
                     * @param \{name.toLowerCase()} the \{name.toLowerCase()}
                     * @return the \{name.toLowerCase()}
                     */
                    public \{name} create(final String userName,
                                           final Locale locale,
                                           final \{name} \{name.toLowerCase()})
                            throws SQLException {
                        \{name.toLowerCase()}.setCreatedBy(userName);
                        this.\{name.toLowerCase()}Store.insert().values(\{name.toLowerCase()}).execute();

                        if (locale != null) {
                            create(\{name.toLowerCase()}.getId(), \{name.toLowerCase()}, locale);
                        }

                        return read(userName, \{name.toLowerCase()}.getId(), locale).get();
                    }

                    private int create(final UUID \{name.toLowerCase()}Id,
                                       final \{name} \{name.toLowerCase()},
                                       final Locale locale) throws SQLException {

                        \{name}Localized \{name.toLowerCase()}Localized = new \{name}Localized();
                        \{name.toLowerCase()}Localized.set\{name}Id(\{name.toLowerCase()}Id);
                        \{name.toLowerCase()}Localized.setLocale(locale.getLanguage());
                        \{name.toLowerCase()}Localized.setTitle(\{name.toLowerCase()}.getTitle());
                        return this.\{name.toLowerCase()}LocalizedStore.insert()
                                .values(\{name.toLowerCase()}Localized)
                                .execute();
                    }

                    /**
                     * Read optional.
                     *
                     * @param userName the user name
                     * @param id       the id
                     * @param locale   the locale
                     * @return the optional
                     */
                    public Optional<\{name}> read(final String userName,
                                                   final UUID id,
                                                   final Locale locale)
                            throws SQLException {

                        if (locale == null) {
                            return this.\{name.toLowerCase()}Store.select(id);
                        }

                        final String select\{name}Query =
                                ""\"
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
                                        from \{name.toLowerCase()} c
                                        left join \{name.toLowerCase()}_localized cl on c.id = cl.\{name.toLowerCase()}_id
                                        where c.id = ?
                                            and (cl.locale is null
                                            or cl.locale = ?
                                            or c.id not in (
                                                select \{name.toLowerCase()}_id
                                                from \{name.toLowerCase()}_localized
                                                where \{name.toLowerCase()}_id = c.id
                                                    and locale = ?
                                            ))
                                        ""\";


                            return \{name.toLowerCase()}Store.select().sql(select\{name}Query)
                                            .param(\{name}LocalizedStore
                                                    .locale(locale.getLanguage()))
                                            .param(\{name}LocalizedStore
                                                    .locale(locale.getLanguage()))
                                            .param(\{name}Store.id(id))
                                            .param(\{name}LocalizedStore
                                                    .locale(locale.getLanguage()))
                                            .param(\{name}LocalizedStore
                                                    .locale(locale.getLanguage()))
                                            .optional();

                    }

                    /**
                     * Update \{name.toLowerCase()}.
                     *
                     * @param id       the id
                     * @param userName the user name
                     * @param locale   the locale
                     * @param \{name.toLowerCase()} the \{name.toLowerCase()}
                     * @return the \{name.toLowerCase()}
                     */
                    public \{name} update(final UUID id,
                                           final String userName,
                                           final Locale locale,
                                           final \{name} \{name.toLowerCase()}) throws SQLException {


                        int updatedRows = 0;

                        if (locale == null) {
                            updatedRows = this.\{name.toLowerCase()}Store.update()
                                    .set(\{name}Store.title(\{name.toLowerCase()}.getTitle()),
                                            \{name}Store.modifiedBy(userName))
                                    .where(\{name}Store.id().eq(id)).execute();
                        } else {
                            updatedRows = this.\{name.toLowerCase()}Store.update()
                                    .set(\{name}Store.modifiedBy(userName))
                                    .where(\{name}Store.id().eq(id)).execute();
                            if (updatedRows != 0) {
                                updatedRows = this.\{name.toLowerCase()}LocalizedStore.update().set(
                                        \{name}LocalizedStore.title(\{name.toLowerCase()}.getTitle()),
                                        \{name}LocalizedStore.locale(locale.getLanguage()))
                                        .where(\{name}LocalizedStore.\{name.toLowerCase()}Id().eq(id)
                                        .and().locale().eq(locale.getLanguage())).execute();

                                if (updatedRows == 0) {
                                    updatedRows = create(id, \{name.toLowerCase()}, locale);
                                }
                            }
                        }


                        if (updatedRows == 0) {

                            throw new IllegalArgumentException("\{name} not found");
                        }

                        return read(userName, id, locale).get();
                    }



                    /**
                     * List list.
                     *
                     * @param userName the user name
                     * @param locale   the locale
                     * @return the list
                     */
                    public List<\{name}> list(final String userName,
                                               final Locale locale) throws SQLException {
                        if (locale == null) {
                            return this.\{name.toLowerCase()}Store.select().execute();
                        }
                        final String list\{name}Query =
                                ""\"
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
                                        from \{name.toLowerCase()} c
                                        left join \{name.toLowerCase()}_localized cl on c.id = cl.\{name.toLowerCase()}_id
                                        where cl.locale is null
                                            or cl.locale = ?
                                        ""\";

                        return \{name.toLowerCase()}Store.select().sql(list\{name}Query)
                                .param(\{name}LocalizedStore.locale(locale.getLanguage()))
                                .param(\{name}LocalizedStore.locale(locale.getLanguage()))
                                .param(\{name}LocalizedStore.locale(locale.getLanguage()))
                                .list();
                    }


                    /**
                     * Delete boolean.
                     *
                     * @param userName the user name
                     * @param id       the id
                     * @return the boolean
                     */
                    public boolean delete(final String userName, final UUID id)
                            throws SQLException {
                        this.\{name.toLowerCase()}LocalizedStore
                                .delete(\{name}LocalizedStore.\{name.toLowerCase()}Id().eq(id))
                                .execute();
                        return this.\{name.toLowerCase()}Store.delete(id) == 1;
                    }


                    /**
                     * Cleaning up all \{name.toLowerCase()}.
                     */
                    public void delete() throws SQLException {
                        this.\{name.toLowerCase()}LocalizedStore.delete().execute();
                        this.\{name.toLowerCase()}Store.delete().execute();
                    }
                }


                                """ ;

        Files.write(
                Paths.get(RAW. "src/main/java/com/gurukulams/core/service/\{name}Service.java".interpolate()),
                SERVICE_TEMPLATE.interpolate().getBytes(),
                StandardOpenOption.CREATE);
    }

    private static void generateDDL(String name,String pluralName) throws IOException {
        StringTemplate DDL_TEMPLATE = RAW
                ."""

                CREATE TABLE \{name} (
                    id UUID PRIMARY KEY,
                    title VARCHAR(55),
                    description TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    created_by VARCHAR(55) NOT NULL,
                    modified_at TIMESTAMP,
                    modified_by VARCHAR(200)
                );

                CREATE TABLE \{name}_localized (
                    \{name.toLowerCase()}_id UUID,
                    locale VARCHAR(8) NOT NULL,
                    title VARCHAR(55),
                    description TEXT,
                    FOREIGN KEY (\{name.toLowerCase()}_id) REFERENCES \{name} (id),
                    PRIMARY KEY(\{name.toLowerCase()}_id, locale)
                );
                """ ;

        Files.write(
                Paths.get("src/main/resources/db/migration/V1__core.sql"),
                DDL_TEMPLATE.interpolate().getBytes(),
                StandardOpenOption.APPEND);
    }
}
