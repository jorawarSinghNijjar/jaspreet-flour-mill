module com.jorawar {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
//    requires org.apache.commons.codec;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires  jackson.annotations;

    opens com.jorawar to javafx.fxml;
    opens com.jorawar.model to com.fasterxml.jackson.databind;

    exports com.jorawar.model;
    exports com.jorawar;
}