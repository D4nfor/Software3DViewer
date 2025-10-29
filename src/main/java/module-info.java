module com.cgvsu {
    requires javafx.controls;
    requires javafx.fxml;

    // Экспортируем все пакеты, которые используются в FXML
    exports com.cgvsu;
    exports com.cgvsu.controller;
    exports com.cgvsu.manager;
    exports com.cgvsu.render_engine;
    exports com.cgvsu.math;
    exports com.cgvsu.model;
    exports com.cgvsu.objreader;

    // Открываем пакеты для рефлексии (FXML загрузка)
    opens com.cgvsu to javafx.fxml;
    opens com.cgvsu.controller to javafx.fxml;
    opens com.cgvsu.model to javafx.fxml;
    opens com.cgvsu.math to javafx.fxml;
    opens com.cgvsu.render_engine to javafx.fxml;
    opens com.cgvsu.objreader to javafx.fxml;
}