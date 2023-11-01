module gurukulams.core {
    requires java.base;
    requires java.sql;
    requires java.naming;
    requires jakarta.validation;
    requires org.hibernate.validator;
    requires com.h2database;

    opens com.gurukulams.core.service;
    opens com.gurukulams.core.payload;
    opens db.migration;

    exports com.gurukulams.core.service;
    exports com.gurukulams.core.payload;
    exports com.gurukulams.core.model;
    exports com.gurukulams.core;
}