module org.mostafayehya {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.mostafayehya to javafx.fxml;
    exports org.mostafayehya;
}