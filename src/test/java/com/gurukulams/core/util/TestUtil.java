package com.gurukulams.core.util;

import com.gurukulams.core.GurukulamsManager;
import org.postgresql.ds.PGSimpleDataSource;

public class TestUtil {
    public static GurukulamsManager gurukulamsManager() {
        PGSimpleDataSource ds = new PGSimpleDataSource() ;
        ds.setURL( "jdbc:postgresql://localhost:5432/gurukulams_db" );
        ds.setUser( "tom" );
        ds.setPassword( "password" );
        ds.setCurrentSchema("gurukulams_db");
        return GurukulamsManager.getManager(ds);
    }
}
