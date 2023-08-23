package com.gurukulams.core.util;

import com.gurukulams.core.GurukulamsManager;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class TestUtil {
    public static GurukulamsManager gurukulamsManager() {

        PGSimpleDataSource ds = new PGSimpleDataSource() ;
        ds.setServerName( "localhost" );
        ds.setUser( "tom" );
        ds.setPassword( "password" );
        ds.setDatabaseName("gurukulams_db");

        return GurukulamsManager.getManager(ds);
    }
}
