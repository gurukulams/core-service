module my.module {
    requires java.base;
    requires java.sql;
    requires java.naming;
    requires org.json;
    requires org.postgresql.jdbc;

    opens com.gurukulams.core.service;
}