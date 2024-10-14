
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

    private final ProfileService profileService;
    private final LearnerService learnerService;

    OrgServiceTest() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

        Validator validator = validatorFactory.getValidator();
        this.learnerService = new LearnerService(TestUtil.dataManager(), validator);
        this.orgService = new OrgService(TestUtil.dataManager());
        this.profileService = new ProfileService(TestUtil.dataManager(),
                this.learnerService,
                new LearnerProfileService(TestUtil.dataManager(), validator),
                this.orgService);
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
        Assertions.assertTrue(orgService.read("hari", org.userHandle(), null).isPresent(), "Created Org");
    }

    @Test
    void createLocalized() throws SQLException {
        final Org org = orgService.create("hari"
                , Locale.GERMAN, anOrg());
        Assertions.assertTrue(orgService.read("hari", org.userHandle(), Locale.GERMAN).isPresent(), "Created Localized Org");
        Assertions.assertTrue(orgService.read("hari", org.userHandle(), null).isPresent(), "Created Org");
    }

    @Test
    void read() throws SQLException {
        final Org org = orgService.create("hari",
                null, anOrg());
        Assertions.assertTrue(orgService.read("hari", org.userHandle(), null).isPresent(),
                "Created Org");
        Assertions.assertEquals(org.imageUrl(),
                this.profileService.read(org.userHandle()).get().profilePicture());

    }

    @Test
    void update() throws SQLException {

        final Org org = orgService.create("hari",
                null, anOrg());
        
        Org newOrg = anOrg();

        String newValue = UUID.randomUUID().toString();
        newOrg.withDescription(newValue);

        Org updatedOrg = orgService
                .update(org.userHandle(), "priya", null, newOrg);
        Assertions.assertEquals(newOrg.title(), updatedOrg.title(), "Updated");

        newOrg.withDescription(UUID.randomUUID().toString());
        updatedOrg = orgService
                .update(org.userHandle(), "priya", null, newOrg);
        Assertions.assertEquals(newOrg.description(), updatedOrg.description(), "Updated");

        newOrg.withImageUrl(UUID.randomUUID().toString());
        updatedOrg = orgService
                .update(org.userHandle(), "priya", null, newOrg);
        Assertions.assertEquals(newOrg.imageUrl(), updatedOrg.imageUrl(), "Updated");

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

        newOrg.withUserHandle(org.userHandle());

        Org updatedOrg = orgService
                .update(org.userHandle(), "priya", Locale.GERMAN, newOrg);

        Assertions.assertEquals(updatedOrg.title(), orgService.read("mani", org.userHandle(), Locale.GERMAN).get().title(), "Updated");
        Assertions.assertNotEquals(updatedOrg.title(), orgService.read("mani", org.userHandle(), null).get().title(), "Updated");


        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            orgService
                    .update(UUID.randomUUID().toString(), "priya", null, newOrg);
        });
    }

    @Test
    void delete() throws SQLException {

        final Org org = orgService.create("hari", null,
                anOrg());
        orgService.delete("mani", org.userHandle());
        Assertions.assertFalse(orgService.read("mani", org.userHandle(), null).isPresent(), "Deleted Org");
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

        Assertions.assertTrue(orgService.register(learner.userHandle(), org.userHandle()));

        Assertions.assertTrue(orgService.isRegistered(learner.userHandle(), org.userHandle()));

        // registering again ? - Invalid
        Assertions.assertThrows(SQLException.class, () -> {
            orgService.register(learner.userHandle(), org.userHandle());
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

        Assertions.assertFalse(orgService.isRegistered(learner2.userHandle(), org.userHandle()));
        Assertions.assertTrue(orgService.register(learner2.userHandle(), org.userHandle()));

        Assertions.assertEquals(1, orgService.getOrganizationsOf(learner2.userHandle(), null).size());


        final Org myOrg = orgService.create(learner2.userHandle(), Locale.GERMAN,
                anOrg());

        // registering for my own org ? - Invalid
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            orgService.register(learner2.userHandle(), myOrg.userHandle());
        });


    }


    /**
     * Gets practice.
     *
     * @return the practice
     */
    public static Org anOrg() {
        Org org = new Org(UUID.randomUUID().toString(),UUID.randomUUID().toString(),
                "HariOrg",
                "Company",
                "HariOrg",
                null,
                "sobhan",
                null,
                null);
        return org;
    }

}
