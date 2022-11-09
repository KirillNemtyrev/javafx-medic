module com.project.medic {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpmime;
    requires java.desktop;

    requires com.google.gson;
    requires org.seleniumhq.selenium.api;
    requires org.seleniumhq.selenium.support;
    requires org.seleniumhq.selenium.chrome_driver;

    opens com.project.medic.entity to com.google.gson;
    opens com.project.medic.dto to com.google.gson;

    opens com.project.medic.controllers to javafx.fxml;
    exports com.project.medic.controllers;

    opens com.project.medic to javafx.fxml;
    exports com.project.medic;
}