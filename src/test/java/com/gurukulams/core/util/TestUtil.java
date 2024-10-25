package com.gurukulams.core.util;

import com.gurukulams.core.DataManager;
import org.postgresql.ds.PGSimpleDataSource;
public class TestUtil {
    public static DataManager dataManager() {
        PGSimpleDataSource ds = new PGSimpleDataSource() ;
        ds.setURL( "jdbc:postgresql://localhost:5432/gurukulams_db" );
        ds.setUser( "tom" );
        ds.setPassword( "password" );
     //   ds.setCurrentSchema("gurukulams_db");
        return DataManager.getManager(ds);
    }

}
