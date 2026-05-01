module com.vis {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.vis to javafx.fxml;
    opens com.vis.controller to javafx.fxml;
    opens com.vis.model to javafx.base;

    exports com.vis;
    exports com.vis.controller;
    exports com.vis.model;
    exports com.vis.dao;
    exports com.vis.util;
}
