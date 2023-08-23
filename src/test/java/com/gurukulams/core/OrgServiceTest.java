package com.gurukulams.core;

import com.gurukulams.core.model.Org;
import com.gurukulams.core.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;


class OrgServiceTest {

    private final OrgService orgService;

    OrgServiceTest() {
        this.orgService = new OrgService(TestUtil.gurukulamsManager());
    }

    /**
     * Before.
     *
     * @throws SQLException the io exception
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
        orgService.deleteAll();
    }


    @Test
    void create() throws SQLException {
        final Org org = orgService.create("hari"
                , newOrg());
        Assertions.assertTrue(orgService.read("hari", org.getId()).isPresent(), "Created org");
    }




    //
    @Test
    void read() throws SQLException {
        final Org org = orgService.create("hari",
                newOrg());
        Assertions.assertTrue(orgService.read("hari", org.getId()).isPresent(),
                "Created org");
    }

    @Test
    void update() throws SQLException {

        final Org org = orgService.create("hari",
                newOrg());
        org.setTitle("Updated");
        Org updatedOrg = orgService
                .update("priya", org.getId(),  org);
        Assertions.assertEquals("Updated", updatedOrg.getTitle(), "Update Org failed");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            orgService
                    .update(UUID.randomUUID().toString(), "priya", org);
        });
    }

    //
    @Test
    void delete() throws SQLException {

        final Org org = orgService.create("hari",
                newOrg());
        orgService.delete("mani", org.getId());
        Assertions.assertFalse(orgService.read("mani", org.getId()).isPresent(), "Deleted org");
    }

//    @Test
//    void list() throws SQLException {
//
//        final Org org = orgService.create("hari",
//                newOrg());
//        Org newOrg = newOrg();
//        orgService.create("hari",
//                newOrg);
//        List<Org> listOfCommunities = orgService.list("hari");
//        Assertions.assertEquals(2, listOfCommunities.size());
//
//    }

    /**
     * Gets new org.
     *
     * @return the org
     */
    Org newOrg() {
        Org org = new Org();
        org.setId(UUID.randomUUID().toString());
        org.setTitle("Hariorg" + org.getId());
        return org;
    }


}