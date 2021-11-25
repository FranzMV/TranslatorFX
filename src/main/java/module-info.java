module com.translatorfx.translatorfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.translatorfx to javafx.fxml;
    opens com.translatorfx.utils to javafx.base;
    exports com.translatorfx;
}