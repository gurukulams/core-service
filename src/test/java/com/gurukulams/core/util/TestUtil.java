package com.gurukulams.core.util;

import com.gurukulams.core.DataManager;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtil {
    public static DataManager dataManager() {
        return DataManager.getManager();
    }

    public static DataSource getDataSource() {
        JdbcDataSource ds = new JdbcDataSource() ;
        ds.setURL("jdbc:h2:./target/test2");
        ds.setUser( "sa" );
        ds.setPassword( "" );
        return ds;
    }

    static {
        try {
            Path.of("./target", "test2.mv.db")
                    .toFile().delete();
            Files.copy(
                    Path.of("./target", "test.mv.db")
                    ,Path.of("./target", "test2.mv.db") );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
