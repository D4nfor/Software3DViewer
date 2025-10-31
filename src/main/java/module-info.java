module com.cgvsu {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    // Экспортируем все пакеты, которые используются в FXML
    exports com.cgvsu;
    exports com.cgvsu.controller;
    exports com.cgvsu.manager;
    exports com.cgvsu.render_engine;
    exports com.cgvsu.math;
    exports com.cgvsu.model;
    exports com.cgvsu.objtools;

    // Открываем пакеты для рефлексии (FXML загрузка)
    opens com.cgvsu to javafx.fxml;
    opens com.cgvsu.controller to javafx.fxml;
    opens com.cgvsu.model to javafx.fxml;
    opens com.cgvsu.math to javafx.fxml;
    opens com.cgvsu.render_engine to javafx.fxml;
    opens com.cgvsu.objtools to javafx.fxml;
    exports com.cgvsu.manager.implementations;
    opens com.cgvsu.manager.implementations to javafx.fxml;
    exports com.cgvsu.manager.interfaces;
    opens com.cgvsu.manager.interfaces to javafx.fxml;
    exports com.cgvsu.render_engine.rendering;
    opens com.cgvsu.render_engine.rendering to javafx.fxml;
}