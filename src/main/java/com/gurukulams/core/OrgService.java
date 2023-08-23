package com.gurukulams.core;

import com.gurukulams.core.model.Org;
import com.gurukulams.core.store.OrgStore;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrgService {
    /**
     * OrgStore.
     */
    private final OrgStore orgStore;

    /**
     * Creates OrgService.
     *
     * @param orgManager
     */
    public OrgService(final GurukulamsManager orgManager) {
        this.orgStore = orgManager.getOrgStore();
    }

    /**
     * Creates a new Org.
     *
     * @param userName
     * @param org
     * @return createdOrg
     */
    public Org create(final String userName,
                               final Org org) throws SQLException {
        org.setCreatedBy(userName);
        return this.orgStore.insert().values(org).returning();
    }

    /**
     * Reads Org by Id.
     *
     * @param userName
     * @param id
     * @return org
     */
    public Optional<Org> read(final String userName,
                                  final String id) throws SQLException {
        return this.orgStore.select(id);
    }

    /**
     * Updates Org By Id.
     *
     * @param userName
     * @param id
     * @param org
     * @return updatedOrg
     */
    public Org update(final String userName,
                          final String id,
                          final Org org) throws SQLException {
        if (id.equals(org.getId())) {
            org.setModifiedBy(userName);
            this.orgStore.update(org);
            return read(userName, org.getId()).get();
        }
        throw new IllegalArgumentException("Invalid Organization Id " + id);
    }

    /**
     * Deletes Org by Id.
     *
     * @param userName
     * @param id
     * @return isDeleted
     */
    public boolean delete(final String userName,
                          final String id) throws SQLException {
        return this.orgStore.delete(id) == 1;
    }

    /**
     * Dletes all the org.
     *
     * @param userName
     * @return noOfOrgsDeleted
     */
    public int delete(final String userName) throws SQLException {
        return this.orgStore.delete().execute();
    }

    /**
     * Lists all the orgs.
     *
     * @param userName
     * @return orgs
     */
    public List<Org> list(final String userName) throws SQLException {
        return this.orgStore.select()
                .execute();
    }

    /**
     * Deletes all the orgs.
     *
     */
    public void deleteAll() throws SQLException {
        this.orgStore.delete()
                .execute();
    }
}
