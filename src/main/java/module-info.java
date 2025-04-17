module com.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.javafx to javafx.fxml;
    exports com.javafx;
}
