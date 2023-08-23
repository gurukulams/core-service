module my.module {
    requires java.base;
    requires java.sql;
    requires java.naming;

    opens com.gurukulams.core.service;
}