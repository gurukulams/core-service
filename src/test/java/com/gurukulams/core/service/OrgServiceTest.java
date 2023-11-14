
package com.gurukulams.core.service;

import com.gurukulams.core.model.Org;
import com.gurukulams.core.payload.Learner;
import com.gurukulams.core.payload.SignupRequest;
import com.gurukulams.core.util.TestUtil;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


class OrgServiceTest {


    private final OrgService orgService;
    private final LearnerService learnerService;

    OrgServiceTest() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

        Validator validator = validatorFactory.getValidator();
        this.learnerService = new LearnerService(TestUtil.gurukulamsManager(), validator);
        this.orgService = new OrgService(TestUtil.gurukulamsManager());
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
        orgService.delete();
        learnerService.delete();
    }


    @Test
    void create() throws SQLException {
        final Org org = orgService.create("hari"
                , null, anOrg());
        Assertions.assertTrue(orgService.read("hari", org.getUserHandle(), null).isPresent(), "Created Org");
    }

    @Test
    void createLocalized() throws SQLException {
        final Org org = orgService.create("hari"
                , Locale.GERMAN, anOrg());
        Assertions.assertTrue(orgService.read("hari", org.getUserHandle(), Locale.GERMAN).isPresent(), "Created Localized Org");
        Assertions.assertTrue(orgService.read("hari", org.getUserHandle(), null).isPresent(), "Created Org");
    }

    @Test
    void read() throws SQLException {
        final Org org = orgService.create("hari",
                null, anOrg());
        Assertions.assertTrue(orgService.read("hari", org.getUserHandle(), null).isPresent(),
                "Created Org");
    }

    @Test
    void update() throws SQLException {

        final Org org = orgService.create("hari",
                null, anOrg());
        
        Org newOrg = anOrg();

        Org updatedOrg = orgService
                .update(org.getUserHandle(), "priya", null, newOrg);
        Assertions.assertEquals(updatedOrg.getTitle(), updatedOrg.getTitle(), "Updated");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            orgService
                    .update(UUID.randomUUID().toString(), "priya", null, newOrg);
        });
    }

    @Test
    void updateLocalized() throws SQLException {

        final Org org = orgService.create("hari",
                null, anOrg());

        Org newOrg = anOrg();

        newOrg.setUserHandle(org.getUserHandle());

        Org updatedOrg = orgService
                .update(org.getUserHandle(), "priya", Locale.GERMAN, newOrg);

        Assertions.assertEquals(updatedOrg.getTitle(), orgService.read("mani", org.getUserHandle(), Locale.GERMAN).get().getTitle(), "Updated");
        Assertions.assertNotEquals(updatedOrg.getTitle(), orgService.read("mani", org.getUserHandle(), null).get().getTitle(), "Updated");


        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            orgService
                    .update(UUID.randomUUID().toString(), "priya", null, newOrg);
        });
    }

    @Test
    void delete() throws SQLException {

        final Org org = orgService.create("hari", null,
                anOrg());
        orgService.delete("mani", org.getUserHandle());
        Assertions.assertFalse(orgService.read("mani", org.getUserHandle(), null).isPresent(), "Deleted Org");
    }

    @Test
    void list() throws SQLException {

        orgService.create("hari", null,
                anOrg());
        orgService.create("hari", null,
                anOrg());
        List<Org> listofCategories = orgService.list("hari", null);
        Assertions.assertEquals(2, listofCategories.size());

    }

    @Test
    void listLocalized() throws SQLException {

        final Org org = orgService.create("hari", Locale.GERMAN,
                anOrg());

        orgService.create("hari", null,
                anOrg());
        List<Org> listofCategories = orgService.list("hari", null);
        Assertions.assertEquals(2, listofCategories.size());

        listofCategories = orgService.list("hari", Locale.GERMAN);
        Assertions.assertEquals(2, listofCategories.size());

    }

    @Test
    void register() throws SQLException {

        final Org org = orgService.create("hari", Locale.GERMAN,
                anOrg());

        SignupRequest signupRequest = LearnerServiceTest.aSignupRequest();
        learnerService.signUp(signupRequest,
                s -> String.valueOf(new StringBuilder(s).reverse()));
        final Learner learner = learnerService.readByEmail(signupRequest.getEmail())
                .get();

        Assertions.assertTrue(orgService.register(learner.userHandle(), org.getUserHandle()));

        Assertions.assertTrue(orgService.isRegistered(learner.userHandle(), org.getUserHandle()));

        // registering again ? - Invalid
        Assertions.assertThrows(SQLException.class, () -> {
            orgService.register(learner.userHandle(), org.getUserHandle());
        });

        // registering for invalid event ? - Invalid
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            orgService.register(learner.userHandle(), UUID.randomUUID().toString());
        });

        signupRequest.setEmail("MYEMAIL@email.com");

        learnerService.signUp(signupRequest,
                s -> String.valueOf(new StringBuilder(s).reverse()));
        final Learner learner2 = learnerService.readByEmail(signupRequest.getEmail())
                .get();

        Assertions.assertFalse(orgService.isRegistered(learner2.userHandle(), org.getUserHandle()));
        Assertions.assertTrue(orgService.register(learner2.userHandle(), org.getUserHandle()));

        final Org myOrg = orgService.create(learner2.userHandle(), Locale.GERMAN,
                anOrg());

        // registering for my own org ? - Invalid
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            orgService.register(learner2.userHandle(), myOrg.getUserHandle());
        });


    }


    /**
     * Gets practice.
     *
     * @return the practice
     */
    Org anOrg() {
        Org org = new Org();
        org.setUserHandle(UUID.randomUUID().toString());
        org.setTitle(UUID.randomUUID().toString());
        org.setDescription("HariOrg");
        return org;
    }

}
