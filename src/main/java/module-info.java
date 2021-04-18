module com.jorawar {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.jorawar to javafx.fxml;
//    opens com.jorawar.model to java.base;

    exports com.jorawar.model;
    exports com.jorawar;
}