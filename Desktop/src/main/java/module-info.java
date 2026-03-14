module com.gilbertoparente.library.desktop {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.gilbertoparente.library.desktop to javafx.fxml;
    exports com.gilbertoparente.library.desktop;
}