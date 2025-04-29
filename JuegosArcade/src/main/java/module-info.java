module com.arcade {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires java.sql;
    
    opens com.arcade to javafx.fxml;
    opens com.arcade.entity to org.hibernate.orm.core;
    
    exports com.arcade;
    exports com.arcade.model;
    exports com.arcade.entity;
    exports com.arcade.factory;
    exports com.arcade.facade;
    exports com.arcade.decorator;
    exports com.arcade.builder;
    exports com.arcade.util;
}
