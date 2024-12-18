package com.gurukulams.core.util;

import com.gurukulams.core.DataManager;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

public class TestUtil {
    public static DataManager dataManager() {
        return DataManager.getManager();
    }

    public static DataSource getDataSource() {
        JdbcDataSource ds = new JdbcDataSource() ;
        ds.setURL("jdbc:h2:./target/test");
        ds.setUser( "sa" );
        ds.setPassword( "" );
        return ds;
    }


}
