package com.gurukulams.core.service;

import com.gurukulams.core.GurukulamsManager;
import com.gurukulams.core.model.Handle;
import com.gurukulams.core.model.Org;
import com.gurukulams.core.store.HandleStore;
import com.gurukulams.core.store.OrgStore;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OrgService {
    /**
     * OrgStore.
     */
    private final OrgStore orgStore;

    /**
     * OrgStore.
     */
    private final HandleStore handleStore;

    /**
     * Creates OrgService.
     *
     * @param orgManager
     */
    public OrgService(final GurukulamsManager orgManager) {
        this.orgStore = orgManager.getOrgStore();
        this.handleStore = orgManager.getHandleStore();
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
        org.setCreatedAt(LocalDateTime.now());
        createHandle(org.getUserHandle());
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
     * @param userHandle
     * @param org
     * @return updatedOrg
     */
    public Org update(final String userName,
                          final String userHandle,
                          final Org org) throws SQLException {
        if (userHandle.equals(org.getUserHandle())) {
            org.setModifiedBy(userName);
            this.orgStore.update().set(org).execute();
            return read(userName, org.getUserHandle()).get();
        }
        throw new IllegalArgumentException("Invalid Organization Id "
                + userHandle);
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
        handleStore.delete(HandleStore.type().eq("Org"))
                .execute();
    }


    private Optional<Handle> createHandle(final String userHandle)
            throws SQLException {
        Handle handle = new Handle();
        handle.setUserHandle(userHandle);
        handle.setType("Org");
        return Optional.of(this.handleStore.insert()
                .values(handle).returning());
    }
}
