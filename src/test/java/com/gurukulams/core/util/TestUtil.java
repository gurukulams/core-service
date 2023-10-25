package com.gurukulams.core.util;

import com.gurukulams.core.GurukulamsManager;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class TestUtil {
    public static GurukulamsManager gurukulamsManager() {
        DataSource dataSource = getH2DataSource();

        ClassLoader classLoader = TestUtil.class.getClassLoader();
        File file = new File(classLoader.getResource("db/migration").getFile());

        Flyway.configure()
                .locations("filesystem:"+file.getAbsolutePath())
                .dataSource(dataSource)
                .load().migrate();

        return GurukulamsManager.getManager(dataSource);
    }

    private static JdbcDataSource getH2DataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:file:./target/"+UUID.randomUUID().getLeastSignificantBits());
        ds.setUser("sa");
        ds.setPassword("sa");
        return ds;
    }

    private static JSONObject getJson(final ResultSet rs, final int index) throws SQLException {
        String jsonText = rs.getString(index);
        return jsonText == null ? null : new JSONObject(jsonText.substring(1,
                jsonText.length() - 1).replace("\\", ""));
    }
}
