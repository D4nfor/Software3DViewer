package com.cgvsu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Software3DViewer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Загружаем основной интерфейс Main.fxml, в котором подключены MenuBar.fxml и TransformPanel.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cgvsu/fxml/Main.fxml"));
        BorderPane viewport = loader.load();

        // Получаем контроллер, если нужно работать напрямую
        // GuiController controller = loader.getController();

        // Создаём сцену
        Scene scene = new Scene(viewport);
        stage.setMinWidth(500);
        stage.setMinHeight(500);

        // Привязываем размеры
//        viewport.prefWidthProperty().bind(scene.widthProperty());
//        viewport.prefHeightProperty().bind(scene.heightProperty());

        // Настройки окна
        stage.setTitle("Software3DViewer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
