module chartreux.applilabv2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens chartreux.applilabv2 to javafx.fxml;
    opens chartreux.applilabv2.controllers to javafx.fxml;
    exports chartreux.applilabv2;
}