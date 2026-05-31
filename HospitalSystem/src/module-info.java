module hospital.system {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens app        to javafx.fxml, javafx.graphics;
    opens controller to javafx.fxml;
    opens model.users        to javafx.fxml;
    opens model.appointments to javafx.fxml;
    opens model.billing      to javafx.fxml;
    opens model.services     to javafx.fxml;
    opens util               to javafx.fxml;

    exports app;
    exports controller;
    exports model.users;
    exports model.appointments;
    exports model.billing;
    exports model.services;
    exports util;
}
